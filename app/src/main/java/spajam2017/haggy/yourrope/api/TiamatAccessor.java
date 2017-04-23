package spajam2017.haggy.yourrope.api;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import spajam2017.haggy.yourrope.train.TrainConnectedCount;
import spajam2017.haggy.yourrope.train.TrainConnectionComplete;
import spajam2017.haggy.yourrope.train.TrainInfo;

/**
 * Web API Accessor
 */

public class TiamatAccessor {

    private final String BASE_URL = "http://13.113.83.238";

    interface UpdateTrainStatusService {
        @Headers({
                "Accept: application/json",
                "Content-type: application/json"
        })
        @POST("/spa/post.php")
        Call<TrainInfo> update(@Body TrainInfo info);
    }

    /**
     * 電車情報を更新する
     *
     * @param info 情報
     */
    public void updateTrainStatus(TrainInfo info) throws IOException {

        final Retrofit retrofit
                = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpdateTrainStatusService service = retrofit.create(UpdateTrainStatusService.class);

        Call<TrainInfo> call = service.update(info);

        Response<TrainInfo> response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("");
        }

        TrainInfo trainInfo = response.body();
    }

    interface GetTrainStatusService {
        @GET("/spa/myname.php")
        Call<TrainInfo> getTrainInfo(@Query("myname") String my_name);
    }

    /**
     * 電車情報を取得する
     *
     * @return 電車情報
     */
    public TrainInfo getTrainStatus(String my_name) throws IOException {

        final Retrofit retrofit
                = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetTrainStatusService getTrainStatusService = retrofit.create(GetTrainStatusService.class);

        Call<TrainInfo> call = getTrainStatusService.getTrainInfo(my_name);

        Response<TrainInfo> response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("");
        }

        return response.body();
    }

    interface GetChainCountService {
        @GET("/spa/number.php")
        Call<TrainConnectedCount> getChainCount(@Query("myname") String my_name);
    }

    /**
     * 自分に連結済みの車両数を取得する
     *
     * @return 連結済みの車両数
     */
    public int getCurrentConnectedTrainCount(String my_name) throws IOException {

        final Retrofit retrofit
                = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetChainCountService getChainCountService = retrofit.create(GetChainCountService.class);

        Call<TrainConnectedCount> call = getChainCountService.getChainCount(my_name);

        Response<TrainConnectedCount> response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("");
        }

        TrainConnectedCount count = response.body();

        return count.chain;
    }

    interface GetAllChainService {
        @GET("/spa/checkall.php")
        Call<TrainConnectionComplete> checkAllChain();
    }

    /**
     * 全台数接続完了か？
     *
     * @return true : 完了!
     */
    public boolean isCompletedConnection() throws IOException {

        final Retrofit retrofit
                = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetAllChainService allChainService = retrofit.create(GetAllChainService.class);

        Call<TrainConnectionComplete> call = allChainService.checkAllChain();

        Response<TrainConnectionComplete> response = call.execute();

        if (!response.isSuccessful()) {
            throw new IOException("");
        }

        TrainConnectionComplete connectionComplete = response.body();

        return connectionComplete.all_chain;
    }
}
