# 🧱 1. What is Clean Architecture — in simple words

Clean Architecture is just a way to organize your project so everything has its own clear job.
You divide your app into 3 main layers:

# 1️⃣ Domain layer – “the brain”

- Contains your app’s main logic (what it’s supposed to do).

- Has:

  - Models (data classes)
  
  - Use cases (specific actions)
  
  - Repository interfaces (rules for getting data, but not how)

It doesn’t know about network, database, or Android — it’s pure Kotlin.

# 2️⃣ Data layer – “the worker”

- This layer actually fetches or saves data (network, database, etc.).

- It implements the repository interface from the Domain layer.

- Example: if Domain says “I need to convert Fahrenheit to Celsius,”
the Data layer says “Okay, I’ll call the SOAP API and bring back the result.”

# 3️⃣ Presentation layer – “the face”

- This layer shows things to the user (UI).

- It has:

  - ViewModel (controls logic and talks to use cases)
  
  - Activity or Fragment (what you actually see)

- It only talks to the Domain layer (through use cases).
- It never talks to SOAP or network directly.

# In short:
```
UI (Presentation)
    ↓
UseCase (Domain)
    ↓
RepositoryImpl (Data)
    ↓
SOAP API (Network)
```

# 🌐 2. What is SOAP

SOAP = Simple Object Access Protocol.
It’s an old web service format that sends data in XML (instead of JSON like REST).

## How it works:

- You send an XML request to a web URL (the SOAP endpoint).

- The server sends back an XML response.

- You read the result from that XML.

Example request to convert temperature:
```
<soap:Envelope>
  <soap:Body>
    <FahrenheitToCelsius>
      <Fahrenheit>75</Fahrenheit>
    </FahrenheitToCelsius>
  </soap:Body>
</soap:Envelope>
```

## Where SOAP is used:

- Old enterprise systems, banks, government, etc.

- You use SOAP only when the API you need gives you SOAP (not REST).

# 🧩 3. Your project — file by file explanation

Here’s what every file in your project does 👇

TemperatureResult.kt (Domain → Model)
```
data class TemperatureResult(val celsiusValue: String)
```

- Holds the converted temperature result.

- Just a container for data.

- Keeps your domain logic clean.
#
TempRepository.kt (Domain → Repository Interface)
```
interface TempRepository {
    suspend fun convertFahrenheit(fahrenheit: String): TemperatureResult
}
```

- Defines what actions are possible (convert temperature).

- Doesn’t say how to do it.

- Data layer will give the real implementation later.
#
ConvertFahrenheitUseCase.kt (Domain → Use Case)
```
class ConvertFahrenheitUseCase(private val repository: TempRepository) {
    suspend operator fun invoke(fahrenheit: String): TemperatureResult =
        repository.convertFahrenheit(fahrenheit)
}
```

- Handles one specific action — convert Fahrenheit.

- Calls the repository method.

- Keeps logic simple and reusable.
#

TempConvertSoapServiceImpl.kt (Data → SOAP Service)
```
class TempConvertSoapServiceImpl {
    fun convertFahrenheitToCelsius(fahrenheit: String): String {
        // Build the SOAP request and get the result from the API
        return resultFromApi
    }
}
```

- This is the part that talks to the actual SOAP API.

- It builds the SOAP XML, sends it, and reads the response.

- You’ll usually use a library like ksoap2 for this.
#

TempRepositoryImpl.kt (Data → Repository Implementation)
```
class TempRepositoryImpl(private val service: TempConvertSoapServiceImpl) : TempRepository {
    override suspend fun convertFahrenheit(fahrenheit: String): TemperatureResult {
        val celsius = service.convertFahrenheitToCelsius(fahrenheit)
        return TemperatureResult(celsius)
    }
}
```

- This class connects your Domain layer and the SOAP service.

- It uses the service to get data, then returns it as a TemperatureResult.
#

AppModule.kt (DI → Hilt setup)
```
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides fun provideSoapService() = TempConvertSoapServiceImpl()
    @Provides fun provideRepository(service: TempConvertSoapServiceImpl): TempRepository =
        TempRepositoryImpl(service)
}
```

- This file tells Hilt (Dependency Injection) how to build your objects.

- So, you don’t manually create them with new.

- Hilt will automatically give your ViewModel what it needs.
#

UiState.kt (Presentation → UI State)
```
sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    data class Success(val result: String) : UiState()
    data class Error(val message: String) : UiState()
}
```

- Represents different screen states:

  - Empty (nothing yet)
  
  - Loading (in progress)
  
  - Success (got result)
  
  - Error (something failed)
#

TempViewModel.kt (Presentation → ViewModel)
```
@HiltViewModel
class TempViewModel @Inject constructor(private val useCase: ConvertFahrenheitUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState

    fun convert(fahrenheit: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = useCase(fahrenheit)
                _uiState.value = UiState.Success(result.celsiusValue)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Conversion failed")
            }
        }
    }
}
```

- Takes user input and handles the flow:

- Show “Loading”

- Call the use case

- Update screen with success or error
#

MainActivity.kt (Presentation → UI)
```
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: TempViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup layout and button click
        // Observe viewModel.uiState and update UI
    }
}
```

- This is your screen.

- It collects data from the ViewModel and updates what the user sees.

- It doesn’t do logic — only shows results.

# 🔁 How everything connects (simple flow)
`````
MainActivity
   ↓ (calls)
TempViewModel
   ↓ (calls)
ConvertFahrenheitUseCase
   ↓ (calls)
TempRepository (interface)
   ↓ (implemented by)
TempRepositoryImpl
   ↓ (calls)
TempConvertSoapServiceImpl
   ↓ (calls)
SOAP API (W3Schools)
`````

Then it returns:
```
SOAP Response → TempRepositoryImpl → UseCase → ViewModel → MainActivity → UI
```

# Example: how data flows (concrete trace)

User types 75 → taps Convert → MainActivity calls vm.convert("75") → ViewModel sets Loading and calls usecase → usecase calls repository → repository runs withContext(IO) → calls TempConvertSoapServiceImpl.convertFahrenheitToCelsius("75") which:

1. builds SoapObject with namespace/method/param

2. builds SoapSerializationEnvelope (VER12 + dotNet as required)

3. HttpTransportSE.call(soapAction, envelope)

4. reads envelope.response → String "23.888..."

5. repository maps to TemperatureResult("23.888...") → usecase → ViewModel → sets UiState.Success("23.888...") → UI collects and shows Result: 23.888... °C.

# 🧭 Data Flow — From User Input to SOAP API and Back
```
┌─────────────────────────────┐
│        MainActivity         │
│  (User enters Fahrenheit)   │
└──────────────┬──────────────┘
               │ calls
               ▼
┌─────────────────────────────┐
│        TempViewModel        │
│  - Shows loading state      │
│  - Calls UseCase            │
└──────────────┬──────────────┘
               │ calls
               ▼
┌─────────────────────────────┐
│  ConvertFahrenheitUseCase   │
│  - Business logic layer     │
│  - Talks only to Repository │
└──────────────┬──────────────┘
               │ calls
               ▼
┌─────────────────────────────┐
│       TempRepository        │
│ (Interface in Domain Layer) │
└──────────────┬──────────────┘
               │ implemented by
               ▼
┌─────────────────────────────┐
│    TempRepositoryImpl       │
│  (Data Layer Implementation)│
│  - Calls SOAP Service       │
└──────────────┬──────────────┘
               │ calls
               ▼
┌─────────────────────────────┐
│ TempConvertSoapServiceImpl  │
│ (Handles SOAP XML Request)  │
│  - Builds SOAP Envelope     │
│  - Sends to API endpoint    │
│  - Parses XML Response      │
└──────────────┬──────────────┘
               │ returns Celsius
               ▼
┌─────────────────────────────┐
│    TempRepositoryImpl       │
│  Wraps data in Model object │
└──────────────┬──────────────┘
               │ returns
               ▼
┌─────────────────────────────┐
│ ConvertFahrenheitUseCase    │
│ Returns TemperatureResult   │
└──────────────┬──────────────┘
               │ updates
               ▼
┌─────────────────────────────┐
│        TempViewModel        │
│  Updates UiState (Success)  │
└──────────────┬──────────────┘
               │ notifies
               ▼
┌─────────────────────────────┐
│        MainActivity         │
│  Shows Celsius result on UI │
└─────────────────────────────┘
```
# 🧩 Quick Summary

| Layer            | Responsibility                  | Example File                                                               |
| ---------------- | ------------------------------- | -------------------------------------------------------------------------- |
| **Presentation** | Show UI, collect user input     | `MainActivity.kt`, `TempViewModel.kt`, `UiState.kt`                        |
| **Domain**       | Core logic, rules               | `TemperatureResult.kt`, `TempRepository.kt`, `ConvertFahrenheitUseCase.kt` |
| **Data**         | Talks to SOAP, gets actual data | `TempConvertSoapServiceImpl.kt`, `TempRepositoryImpl.kt`                   |


