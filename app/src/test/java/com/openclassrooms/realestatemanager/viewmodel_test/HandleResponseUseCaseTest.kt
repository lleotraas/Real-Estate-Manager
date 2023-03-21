package com.openclassrooms.realestatemanager.viewmodel_test

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.features_real_estate.data.remote.AutocompleteApi
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Adresse
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Geometry
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.adresse.Properties
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.autocomplete.HandleResponse
import com.openclassrooms.realestatemanager.features_real_estate.domain.use_case.real_estate.*
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class HandleResponseUseCaseTest {

    lateinit var useCase: HandleResponse

    @Before
    fun setUp() {
        useCase = HandleResponse()
    }

    @Test
    fun `get a list from handle AutoComplete response`() = runBlocking {
        val geometry = mock(Geometry::class.java)
        val properties = mock(Properties::class.java)
        val test = Adresse(
            "test",

        )
//        if(test){
//            assert(test)
//        }

    }

    class MockInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            if (BuildConfig.DEBUG) {
                val uri = chain.request().url.toUri().toString()
                val responseString = when {
                    uri.endsWith("search") ->
                        GET_LIST_OF_ADDRESS
                    else -> ""
                }
                return chain.proceed(chain.request())
                    .newBuilder()
                    .code(200)
                    .protocol(Protocol.HTTP_2)
                    .message(responseString)
                    .body(ResponseBody.create(
                        "application/json".toMediaTypeOrNull(),
                        responseString.toByteArray()))
                    .addHeader("content-type", "application/json")
                    .build()
            } else {
                throw IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                        "bound to be used only with DEBUG mode")
            }
        }


    }

    object NetworkClient {
        fun create() : AutocompleteApi {
            return Retrofit.Builder()
                .client(OkHttpClient.Builder().addInterceptor(MockInterceptor()).build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api-adresse.data.gouv.fr/")
                .build().create(AutocompleteApi::class.java)
        }
    }
}

const val GET_LIST_OF_ADDRESS = """{"type":"FeatureCollection","version":"draft","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[-61.065388,14.604142]},"properties":{"label":"9 Bd Chevalier Sainte Marthe 97200 Fort-de-France","score":0.8706045454545454,"housenumber":"9","id":"97209_0390_00009","name":"9 Bd Chevalier Sainte Marthe","postcode":"97200","citycode":"97209","x":708397.73,"y":1615430.45,"city":"Fort-de-France","context":"972, Martinique","type":"housenumber","importance":0.57665,"street":"Bd Chevalier Sainte Marthe"}},{"type":"Feature","geometry":{"type":"Point","coordinates":[-0.003876,49.32302]},"properties":{"label":"9 Boulevard Pitre Chevalier 14640 Villers-sur-Mer","score":0.4127463636363636,"housenumber":"9","id":"14754_1380_00009","name":"9 Boulevard Pitre Chevalier","postcode":"14640","citycode":"14754","x":481620.48,"y":6917869.64,"city":"Villers-sur-Mer","context":"14, Calvados, Normandie","type":"housenumber","importance":0.54021,"street":"Boulevard Pitre Chevalier"}},{"type":"Feature","geometry":{"type":"Point","coordinates":[1.783856,49.157064]},"properties":{"label":"Boulevard des Chevaliers 95420 Magny-en-Vexin","score":0.35643440559440565,"id":"95355_0044","name":"Boulevard des Chevaliers","postcode":"95420","citycode":"95355","x":611285.11,"y":6895938.25,"city":"Magny-en-Vexin","context":"95, Val-d'Oise, Île-de-France","type":"street","importance":0.45924,"street":"Boulevard des Chevaliers"}},{"type":"Feature","geometry":{"type":"Point","coordinates":[2.914052,48.954506]},"properties":{"label":"Boulevard du Chevalier Bayard 77100 Meaux","score":0.35514818181818175,"id":"77284_0219","name":"Boulevard du Chevalier Bayard","postcode":"77100","citycode":"77284","x":693705.53,"y":6872730.86,"city":"Meaux","context":"77, Seine-et-Marne, Île-de-France","type":"street","importance":0.60663,"street":"Boulevard du Chevalier Bayard"}},{"type":"Feature","geometry":{"type":"Point","coordinates":[2.914967,48.954331]},"properties":{"label":"Boulevard du Chevalier Bayard 77100 Meaux","score":0.35199818181818177,"id":"77284_481ggv","name":"Boulevard du Chevalier Bayard","postcode":"77100","citycode":"77284","x":693772.51,"y":6872711.33,"city":"Meaux","context":"77, Seine-et-Marne, Île-de-France","type":"street","importance":0.57198,"street":"Boulevard du Chevalier Bayard"}}],"attribution":"BAN","licence":"ETALAB-2.0","query":"9 bd chevalier","limit":5}"""