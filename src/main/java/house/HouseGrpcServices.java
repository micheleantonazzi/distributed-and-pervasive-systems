package house;

import house.services.HouseServicesGrpc.HouseServicesImplBase;
import house.services.HouseServicesOuterClass.Empty;
import house.services.HouseServicesOuterClass.Response;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs.StatisticHouseMsg;

public class HouseGrpcServices extends HouseServicesImplBase {

    @Override
    public void hello(HouseInfoMsg house, StreamObserver<Response> responseObserver){
        HousesAndStatistics.getInstance().addHouse(house);

        responseObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());

        responseObserver.onCompleted();


    }

    @Override
    public void goodbye(HouseInfoMsg house, StreamObserver<Response> responseObserver){

        HousesAndStatistics.getInstance().removeHouse(house);

        responseObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());

        responseObserver.onCompleted();
    }

    @Override
    public void stopCoordinate(Empty e, StreamObserver<Response> responseObserver){
        Coordinator.getInstance().notCoordinator();

        responseObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<StatisticHouseMsg> sendStatistic(final StreamObserver<Response> responseObserver){

        return new StreamObserver<StatisticHouseMsg>() {
            @Override
            public void onNext(StatisticHouseMsg statisticHouseMsg) {
                HousesAndStatistics.getInstance().addStatistic(statisticHouseMsg.getHouseInfo(), statisticHouseMsg.getStatistic());
            }

            @Override
            public void onError(Throwable throwable) {
                //System.out.println(throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println("House exit correctly");
                responseObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void acquireBoost(HouseInfoMsg house, StreamObserver<Response> streamObserver){
        BoostCoordinator.getInstance().request(house, streamObserver);
    }
}
