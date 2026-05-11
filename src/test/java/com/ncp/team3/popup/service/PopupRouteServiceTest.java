package com.ncp.team3.popup.service;

import com.ncp.team3.popup.controller.dto.request.PopupRouteOptimizeRequest;
import com.ncp.team3.popup.controller.dto.response.PopupRouteOptimizeResponse;
import com.ncp.team3.popup.controller.dto.response.RouteResponse;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.infrastructure.NaverDirectionsClient;
import com.ncp.team3.popup.port.PopupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopupRouteServiceTest {

    @Mock
    private PopupRepository popupRepository;

    @Mock
    private NaverDirectionsClient naverDirectionsClient;

    @InjectMocks
    private PopupRouteService popupRouteService;

    @Test
    void optimizeRouteKeepsAllPopupsWhenAllCoordinatesAreDifferent() {
        Popup start = popup(1L, 127.0, 37.0);
        Popup targetA = popup(2L, 127.1, 37.1);
        Popup targetB = popup(3L, 127.2, 37.2);

        when(popupRepository.findById(1L)).thenReturn(Optional.of(start));
        when(popupRepository.findAllById(any(Iterable.class))).thenReturn(List.of(targetA, targetB));
        when(naverDirectionsClient.getDrivingRoute(any())).thenReturn(RouteResponse.empty());

        PopupRouteOptimizeResponse response = popupRouteService.optimizeRoute(
                new PopupRouteOptimizeRequest(1L, List.of(2L, 3L))
        );

        assertThat(response.orderedPopups()).hasSize(3);
        assertThat(response.orderedPopups()).extracting("order").containsExactly(1, 2, 3);

        ArgumentCaptor<List<Popup>> captor = ArgumentCaptor.forClass(List.class);
        verify(naverDirectionsClient).getDrivingRoute(captor.capture());
        assertThat(captor.getValue()).hasSize(3);
    }

    @Test
    void optimizeRouteRemovesDuplicateCoordinatesOnlyForNaverRequest() {
        Popup start = popup(1L, 127.0, 37.0);
        Popup sameCoordinate = popup(2L, 127.0, 37.0);
        Popup other = popup(3L, 127.2, 37.2);

        when(popupRepository.findById(1L)).thenReturn(Optional.of(start));
        when(popupRepository.findAllById(any(Iterable.class))).thenReturn(List.of(sameCoordinate, other));
        when(naverDirectionsClient.getDrivingRoute(any())).thenReturn(RouteResponse.empty());

        PopupRouteOptimizeResponse response = popupRouteService.optimizeRoute(
                new PopupRouteOptimizeRequest(1L, List.of(2L, 3L))
        );

        assertThat(response.orderedPopups()).hasSize(3);
        assertThat(response.orderedPopups()).extracting("popupId").containsExactly(1L, 2L, 3L);
        assertThat(response.orderedPopups()).extracting("order").containsExactly(1, 2, 3);

        ArgumentCaptor<List<Popup>> captor = ArgumentCaptor.forClass(List.class);
        verify(naverDirectionsClient).getDrivingRoute(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue()).extracting(Popup::getId).containsExactly(1L, 3L);
    }

    @Test
    void optimizeRouteDoesNotCallNaverWhenAllCoordinatesAreSame() {
        Popup start = popup(1L, 127.0, 37.0);
        Popup sameA = popup(2L, 127.0, 37.0);
        Popup sameB = popup(3L, 127.00000001, 37.00000001);

        when(popupRepository.findById(1L)).thenReturn(Optional.of(start));
        when(popupRepository.findAllById(any(Iterable.class))).thenReturn(List.of(sameA, sameB));

        PopupRouteOptimizeResponse response = popupRouteService.optimizeRoute(
                new PopupRouteOptimizeRequest(1L, List.of(2L, 3L))
        );

        assertThat(response.orderedPopups()).hasSize(3);
        assertThat(response.orderedPopups()).extracting("popupId").containsExactly(1L, 2L, 3L);
        assertThat(response.orderedPopups()).extracting("order").containsExactly(1, 2, 3);
        assertThat(response.route().path()).isEmpty();

        verify(naverDirectionsClient, never()).getDrivingRoute(any());
    }

    private Popup popup(Long id, double longitude, double latitude) {
        Popup popup = org.mockito.Mockito.mock(Popup.class);
        when(popup.getId()).thenReturn(id);
        when(popup.getLongitude()).thenReturn(longitude);
        when(popup.getLatitude()).thenReturn(latitude);
        when(popup.getTitle()).thenReturn("popup-" + id);
        when(popup.getImageUrl()).thenReturn("image-" + id);
        when(popup.getAddress()).thenReturn("address-" + id);
        when(popup.getStartDate()).thenReturn(LocalDate.of(2026, 5, 1));
        when(popup.getEndDate()).thenReturn(LocalDate.of(2026, 5, 31));
        when(popup.getStatus()).thenReturn("OPEN");
        return popup;
    }
}
