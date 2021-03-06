package ch.aaap.harvestclient.service;

import java.util.Map;

import ch.aaap.harvestclient.domain.ClientContact;
import ch.aaap.harvestclient.domain.pagination.PaginatedList;
import ch.aaap.harvestclient.domain.param.ClientContactUpdateInfo;
import retrofit2.Call;
import retrofit2.http.*;

public interface ClientContactService {

    String basePath = "contacts";
    String id = "id";
    String path = basePath + "/{" + id + "}";

    @GET(basePath)
    Call<PaginatedList> list(@QueryMap Map<String, Object> options);

    @GET(path)
    Call<ClientContact> get(@Path(id) long clientContactId);

    @POST(basePath)
    Call<ClientContact> create(@Body ClientContact creationInfo);

    @PATCH(path)
    Call<ClientContact> update(@Path(id) long clientContactId, @Body ClientContactUpdateInfo updateInfo);

    @DELETE(path)
    Call<Void> delete(@Path(id) long clientContactId);
}
