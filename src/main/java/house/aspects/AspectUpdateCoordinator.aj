package house.aspects;

import house.Coordinator;
import messages.HouseMsgs.HouseInfoMsg;

public aspect AspectUpdateCoordinator {

    after(HouseInfoMsg house): execution(* house.HousesAndStatistics.addHouse(..)) && args(house){
        Coordinator.getInstance().addHouse(house);
    }

    after(HouseInfoMsg house): execution(* house.HousesAndStatistics.removeHouse(..)) && args(house){
        Coordinator.getInstance().removeHouse(house);
    }
}
