package insa.lyon.justif.model;

import insa.lyon.justif.generated.model.Request;
import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.generated.model.Trip;

import java.util.LinkedList;
import java.util.Objects;

public class TourTSP {

    private final Tour tour;

    private LinkedList<String> previousIntersection = new LinkedList<>();

    private LinkedList<String> intersectionIds = new LinkedList<>();

    public TourTSP(Tour tour) {
        this.tour = tour;

        for(Trip trip: tour.getTrips()){
            intersectionIds.add(trip.getFrom());
        }
    }

    public LinkedList<String> getIntersections() {
        return intersectionIds;
    }

    public void setIntersectionIds(LinkedList<String> intersectionIds) {
        this.intersectionIds = new LinkedList<>(intersectionIds);
    }

    public Double getLengthByIntersections() {
        Double length = 0.;
        for (int i = 0; i < intersectionIds.size() - 1; i++) {
            length += tour.getPlanning().getShortestTrips().get(intersectionIds.get(i) + intersectionIds.get(i+1)).getDistance();
        }
        length += tour.getPlanning().getShortestTrips().get(intersectionIds.getLast() + intersectionIds.getFirst()).getDistance();
        return length;
    }

    public Double getTripLength(String intersectionA, String intersectionB) {
        return tour.getPlanning().getShortestTrips().get(intersectionA + intersectionB).getDistance();
    }

    public void swap(int a, int b) {
        previousIntersection = new LinkedList<>(intersectionIds);
        String x = intersectionIds.get(a);
        String y = intersectionIds.get(b);
        intersectionIds.set(a, y);
        intersectionIds.set(b, x);
    }

    public void swap() {
        int a, b;
        boolean isValidSwap = false;
        while (!isValidSwap) {
            a = randomIndex();
            b = randomIndex();
            swap(a, b);
            isValidSwap = isPickupBeforeDelivery();
            if (!isValidSwap) {
                revert();
            }
        }
    }

    public void revert() {
        intersectionIds = previousIntersection;
    }

    private int randomIndex() {
        return (int) (Math.random() * (intersectionIds.size() - 1)) + 1;
    }

    private boolean isPickupBeforeDelivery() {
        for (Request request: tour.getPlanning().getRequests()) {
            int pickupIndex = findPointIndex(request.getPickupAddress());
            int deliveryIndex = findPointIndex(request.getDeliveryAddress());
            if (pickupIndex > deliveryIndex) {
                return false;
            }
        }
        return true;
    }

    private int findPointIndex(String intersectionId) {
        int index = 1;
        for (String intersection : intersectionIds) {
            if (Objects.equals(intersectionId, intersection)) return index;
            index++;
        }
        return index;
    }

    /**
     * @return null if it's a pickup point or pickup's point id if it's a delivery point
     */
    public String getPickupPoint(String id) {
        for (Request request : tour.getPlanning().getRequests()) {
            if (Objects.equals(request.getPickupAddress(), id)) {
                return null;
            } else if (Objects.equals(request.getDeliveryAddress(), id)) {
                return request.getPickupAddress();
            }
        }
        return null;
    }
}
