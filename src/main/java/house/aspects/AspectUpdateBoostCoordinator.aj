package house.aspects;

import house.BoostCoordinator;
import messages.HouseMsgs;

public aspect AspectUpdateBoostCoordinator {

    after(HouseMsgs.HouseInfoMsg house): execution(* house.HousesAndStatistics.removeHouse(..)) && args(house){
        BoostCoordinator.getInstance().removeHouse(house);
    }
}
