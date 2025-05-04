import com.domatix.yevbes.nucleus.core.OdooUser
import com.domatix.yevbes.nucleus.core.entities.session.info.GetSessionInfo
import com.domatix.yevbes.nucleus.core.entities.session.info.GetSessionInfoReqBody
import com.domatix.yevbes.nucleus.core.utils.Retrofit2Helper
import com.domatix.yevbes.nucleus.core.utils.android.ktx.ResponseObserver
import com.domatix.yevbes.nucleus.core.web.session.info.GetSessionInfoRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

object Odoo {

    var protocol: Retrofit2Helper.Protocol = Retrofit2Helper.Protocol.HTTPS
        set(value) {
            field = value
            retrofit2Helper.protocol = value
        }

    var host: String = ""
        set(value) {
            field = value
            retrofit2Helper.host = value
        }

    var user: OdooUser = OdooUser()
        set(value) {
            field = value
            protocol = value.protocol
            host = value.host
        }

    private val retrofit2Helper = Retrofit2Helper(protocol, host)

    // ⬇️ Añadimos aquí la lista de versiones soportadas
    val supportedOdooVersions = listOf("13", "14", "15", "16")

    private val retrofit
        get() = retrofit2Helper.retrofit

    private var jsonRpcId: String = "0"
        get() {
            field = (field.toInt() + 1).toString()
            if (user.id > 0) {
                return "r$field"
            }
            return field
        }

    fun getSessionInfo(callback: ResponseObserver<GetSessionInfo>.() -> Unit) {
        val request = retrofit.create(GetSessionInfoRequest::class.java)
        val requestBody = GetSessionInfoReqBody(id = jsonRpcId)
        val observable = request.getSessionInfo(requestBody)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ResponseObserver<GetSessionInfo>().apply(callback))
    }
}
