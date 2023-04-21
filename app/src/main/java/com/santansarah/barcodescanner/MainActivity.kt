package com.santansarah.barcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.santansarah.barcodescanner.data.remote.FoodApi
import com.santansarah.barcodescanner.data.remote.ItemListing
import com.santansarah.barcodescanner.data.remote.formatToGrams
import com.santansarah.barcodescanner.ui.theme.BarcodeScannerTheme
import com.santansarah.barcodescanner.ui.theme.primary
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var foodApi: FoodApi

    @Inject
    lateinit var ktorClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            BarcodeScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = primary
                ) {

                    /**
                     * Product: https://world.openfoodfacts.org/product/0078742366951/halves-pieces-great-value
                     * vs a complete profile: https://world.openfoodfacts.org/product/0029000016071/cashew-halves-pieces-planters
                     */

                    runBlocking {
                        val stringResponse = foodApi.getInfoByBarCodeString()
                        println(stringResponse.string())
                    }

                    /*runBlocking {
                        val itemListing = foodApi.getInfoByBarCode(
                            barCode = "0078742366951", fields = "brands,nutriments"
                        )
                        println(itemListing.toString())
                    }*/

                    /*runBlocking {
                        val itemListing = foodApi.getInfoByBarCodeRetrofit(
                            barCode = "0078742366951", fields = "brands,nutriments"
                        )
                        println(itemListing.toString())
                        println(itemListing.product?.nutriments?.carbohydrates.formatToGrams() + "g")
                    }*/

                }
            }
        }
    }
}
