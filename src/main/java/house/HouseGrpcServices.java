package house;

import house.services.HouseServicesGrpc.HouseServicesImplBase;
import house.services.HouseServicesOuterClass.Response;
import io.grpc.stub.StreamObserver;
import messages.HouseMsgs.HouseInfoMsg;
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
}
