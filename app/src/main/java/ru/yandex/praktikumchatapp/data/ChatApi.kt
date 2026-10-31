package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.random.Random

class ChatApi {

    companion object {
        private const val MAXIMUM_RESPONSE_DELAY = 5000L
    }

    private val responses = listOf(
        "Отлично!",
        "Понял.",
        "Звучит здорово!",
        "Так точно!",
        "Без проблем!",
        "Уже в работе.",
        "Интересно...",
        "Хм... подумаю.",
        "Давай попробуем!",
        "Это мысль!",
        "Как скажешь.",
        "Именно так!",
        "Продолжай!",
        "Не вопрос.",
        "Забавно!",
        "Окей-докей!",
        "По рукам!",
        "Всё будет!",
        "Уже бегу!",
        "Сделано!",
        "Есть!",
        "Понял-принял!",
        "Заметано!",
        "Ого!",
        "Круто!",
        "Вот это да!",
        "Невероятно!",
        "Супер!",
        "Класс!",
        "Восхитительно!",
        "Блеск!",
        "Молодец!",
        "Так держать!",
        "Ура!",
        "Ага!",
        "Точно!",
        "Именно!",
        "Верно!",
        "Согласен!",
        "Поддерживаю!",
        "Конечно!",
        "Разумеется!",
        "Безусловно!",
        "Абсолютно!",
        "Идеально!",
        "Прекрасно!",
        "Замечательно!",
        "Великолепно!",
        "Потрясающе!",
        "Невероятно!",
        "Фантастика!",
        "Шикарно!",
        "Браво!",
        "Отлично!",
        "Здорово!",
        "Хорошо!",
        "Ладно!",
        "Пойдет!",
        "Нормально!",
        "Ничего!",
        "Бывает!",
        "Всё в порядке!",
        "Не беда!",
        "Не страшно!",
        "Забудем!",
        "Проехали!",
        "Всё хорошо!",
        "Не переживай!",
        "Успокойся!",
        "Расслабься!"
    )

    fun getReply(): Flow<String> = flow {
        while (currentCoroutineContext().isActive) {
            delay(Random.nextLong(MAXIMUM_RESPONSE_DELAY))
            if (Random.nextBoolean()) {
                throw Exception("Ошибка запроса", Throwable("Something went wrong"))
            }
            emit(responses.random())
        }
    }
}