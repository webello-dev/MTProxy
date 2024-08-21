package ir.webello.mtporxy;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api_app/")
    Call<ApiResponse> getData(@Query("type") int type);
}

