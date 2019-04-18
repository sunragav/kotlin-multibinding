import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
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

    init {
        DaggerAppComponent.create().inject(this)
    }

    fun present(): String {
        var a = ""
        decorators.forEach { a = "$a ${it.decorate()}" }
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
    fun getDecor2(@InfoStr2 str1: String, @InfoStr3 str2: String) = setOf(
        HiDecorator(InfoModule.getInfo(str1)),
        ByeDecorator(InfoModule.getInfo(str1)),
        NamasteDecorator(InfoModule.getInfo(str2))
    )
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
