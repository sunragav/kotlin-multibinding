import dagger.Component
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InfoStr1

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InfoStr2

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InfoStr3

fun main() {
    println(MainClass().present())
}

class MainClass {

    @Inject
    lateinit var decorators: Set<@JvmSuppressWildcards IDecorator>

    @Inject
    lateinit var decoratorsMap: Map<String, @JvmSuppressWildcards IDecorator>

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun present(): String {
        var a = ""
        decorators.forEach { a = "$a${it.decorate()}" }
        a+="\n"
        decoratorsMap.forEach{a="$a ${it.key} -> ${it.value}\n"}
        return a
    }
}

interface IDecorator {
    fun decorate(): String
}

class HiDecorator @Inject constructor(val info: Info) : IDecorator {
    override fun decorate(): String {
        return " Hi ${info.text}!!"
    }
}


class ByeDecorator @Inject constructor(val info: Info) : IDecorator {
    override fun decorate(): String {
        return " Bye ${info.text}!!"
    }
}

class NamasteDecorator @Inject constructor(val info: Info) : IDecorator {
    override fun decorate(): String {
        return " Namaste ${info.text}!!"
    }
}

class Info @Inject constructor(val text: String)
@Module
object AppModule {
    @Provides
    @JvmStatic
    @ElementsIntoSet
    fun getDecor2(@InfoStr1 str1: String, @InfoStr2 str2: String, @InfoStr3 str3: String) = setOf(
        HiDecorator(InfoModule.getInfo(str1)),
        ByeDecorator(InfoModule.getInfo(str2)),
        NamasteDecorator(InfoModule.getInfo(str3))
    )

    @Provides
    @JvmStatic
    @IntoMap
    @StringKey("HiDecorator")
    fun getHiDec(@InfoStr1 str1: String):IDecorator =
        HiDecorator(InfoModule.getInfo(str1))

    @Provides
    @JvmStatic
    @IntoMap
    @StringKey("ByeDecorator")
    fun getByeDec(@InfoStr2 str2: String):IDecorator= ByeDecorator(InfoModule.getInfo(str2))

    @Provides
    @JvmStatic
    @IntoMap
    @StringKey("NamasteDecorator")
    fun getNamasteDec(@InfoStr3 str3: String):IDecorator = NamasteDecorator(InfoModule.getInfo(str3))
}

@Module
object InfoModule {
    @Provides
    @InfoStr1
    @JvmStatic
    fun getStr1() = "Kotlin"

    @Provides
    @InfoStr2
    @JvmStatic
    fun getStr2() = "Scala"

    @Provides
    @InfoStr3
    @JvmStatic
    fun getStr3() = "Java"

    @Provides
    @JvmStatic
    fun getInfo(@InfoStr1 str: String): Info = Info(str)
}

@Component(modules = [AppModule::class, InfoModule::class])
interface AppComponent {
    fun inject(mainClass: MainClass)
}
