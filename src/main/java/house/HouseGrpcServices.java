package house;

import house.services.HouseServicesGrpc.HouseServicesImplBase;
import house.services.HouseServicesOuterClass.Response;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;
import messages.StatisticMsgs.StatisticHouseMsg;
import utility.HousesAndStatistics;

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
    public StreamObserver<StatisticHouseMsg> sendStatistic(StreamObserver<Response> streamObserver){

        return new StreamObserver<StatisticHouseMsg>() {
            @Override
            public void onNext(StatisticHouseMsg statisticHouseMsg) {
                HousesAndStatistics.getInstance().addStatistic(statisticHouseMsg.getHouseInfo(), statisticHouseMsg.getStatistic());
                System.out.println("Statistica ricevuta dalla casa " + statisticHouseMsg.getHouseInfo().getId());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Eccezione\n" + throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println("Casa uscita correttamente");
                streamObserver.onNext(Response.newBuilder().setStatus(Response.Status.OK).build());
                streamObserver.onCompleted();
            }
        };
    }
}
