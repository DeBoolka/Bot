package dikanev.nikita.bot.service.client.parameter;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.NoSuchElementException;

public interface Parameter{

    /**
     *
     * @param parameter название параметра
     * @return Подпараметр типа {@code Parameter}
     */
    Parameter getParameter(String parameter);

    /**
     *
     * @return представление ввиде типа параметра
     */
    String getContent();

    /**
     *
     * @param param название параметра
     * @return {@code List<String>} со значениями входного параметра.
     *         {@code null}, если значение отсутствует
     * @throws IllegalFormatException если значение невозможно преобразовать в список строк
     */
    List<String> get(String param);

    /**
     *
     * @param param название параметра
     * @param def список со значениями по умолчанию
     * @return {@code List<String>} со значениями входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в список строк
     */
    List<String> getOrDefault(String param, List<String> def);

    /**
     *
     * @param param название параметра
     * @return {@code List<Integer>} со значениями входного параметра.
     *         {@code null}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в список чисел
     * @throws NumberFormatException если значение невозможно преобразовать в список чисел
     */
    List<Integer> getInt(String param);

    /**
     *
     * @param param название параметра
     * @param def значение по умолчанию
     * @return {@code List<Integer>} со значениями входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в список чисел
     * @throws NumberFormatException если значение невозможно преобразовать в список чисел
     */
    List<Integer> getIntOrDefault(String param, List<Integer> def);

    /**
     * Ищет первое попавшееся значение входного параметра
     *
     * @param param название параметра
     * @return значение входного параметра.
     *         {@code null}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в строку
     */
    String getF(String param);

    /**
     * Ищет первое попавшееся значение входного параметра.
     *
     * @param param название параметра
     * @param def значение по умолчанию
     * @return значение входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в строку
     */
    String getFOrDefault(String param, String def);

    /**
     * Ищет первое попавшееся значение входного параметра.
     *
     * @param param название параметра
     * @return значение входного параметра.
     * @throws IllegalFormatException, если значение невозможно преобразовать в число
     * @throws NumberFormatException, если значение невозможно преобразовать в список чисел
     * @throws NoSuchElementException, если значение отсутствует
     */
    int getIntF(String param) throws NoSuchFieldException;

    /**
     * Ищет первое попавшееся значение входного параметра.
     *
     * @param param название параметра
     * @param def значение по умолчанию
     * @return значение входного параметра.
     *         {@code def}, если значение отсутствует.
     * @throws IllegalFormatException если значение невозможно преобразовать в число
     * @throws NumberFormatException если значение невозможно преобразовать в список чисел
     */
    int getIntFOrDefault(String param, int def);

    /**
     * Устанавливает новые параметры
     *
     * @param parameters тело параметров
     * @return текущий параметр
     */
    Parameter set(String parameters);

    /**
     * Устанавливает значение параметра. Старое значение, если оно было - удаляет
     *
     * @param param параметр
     * @param val значение параметра
     * @return Новый объект типа {@code Parameter}
     */
    Parameter set(String param, String val);

    /**
     * Устанавливает значение параметра. Старое значение, если оно было - удаляет
     *
     * @param param параметр
     * @param val значение параметра
     * @return Новый объект типа {@code Parameter}
     */
    Parameter set(String param, List<String> val);

    /**
     * Добавляет значение параметру. Если парамтра небыло, он создается.
     *
     * @param param параметр
     * @param val значение параметра
     * @return Новый объект типа {@code Parameter}
     */
    Parameter add(String param, String val);

    /**
     * Добавляет значение параметру. Если парамтра небыло, он создается.
     *
     * @param param параметр
     * @param val значение параметра
     * @return Новый объект типа {@code Parameter}
     */
    Parameter add(String param, List<String> val);

    /**
     *
     * @param param название параметра
     * @return {@code true}, если есть такой параметр
     */
    boolean contains(String param);

    /**
     *
     * @param params название параметров
     * @return {@code true}, если есть один из параметров
     */
    boolean contains(String... params);

    /**
     *
     * @param params название параметров
     * @return {@code true}, если есть параметры
     */
    boolean containsAll(List<String> params);

    /**
     *
     * @param params название параметров
     * @return {@code true}, если есть параметры
     */
    boolean containsAll(String... params);

    /**
     * Проверяет, что все значения присутствуют в параметре
     *
     * @param param название параметра
     * @param vals проверяемы значения
     * @return {@code true}, если все значения есть в параметре
     */
    boolean containsAllVal(String param, List<String> vals);

    /**
     * Проверяет, что значение присутствует в параметре
     *
     * @param param название параметра
     * @param val проверяемое значение
     * @return {@code true}, если значения есть в параметре
     */
    boolean containsVal(String param, String val, String ... values);

    /**
     * Проверяет, что значение присутствует в параметре
     *
     * @param param название параметра
     * @param val проверяемое значение
     * @return {@code true}, если значения есть в параметре
     */
    boolean containsVal(String param, int val);

    /**
     * Очищает все параметры
     *
     * @return новый объект типа {@code Parameter}
     */
    Parameter clear();

    /**
     * Удаляет параметр
     *
     * @param param название параметра
     * @return Новый объект типа {@code Parameter}
     */
    Parameter remove(String param);

    /**
     * Удаляет параметр
     *
     * @param param название параметра
     * @param index index значения в параметре
     * @return Новый объект типа {@code Parameter}
     *
     */
    Parameter remove(String param, int index);

    /**
     * Удаляет параметр
     *
     * @param param название параметра
     * @param val значение в параметре
     * @return Новый объект типа {@code Parameter}
     *
     */
    Parameter remove(String param, String val);

    /**
     * Сохраняет текующее состояние параметров
     *
     * @return текущий @{code Parameter}
     */
    Parameter transaction();

    /**
     * Возвращает состояние параметров к сохраненному ранее
     *
     * @return текущий @{code Parameter}
     */
    Parameter rollback();

    /**
     * Удаляет сохраненное раннее состояние
     *
     * @return текущий @{code Parameter}
     */
    Parameter endTransaction();

    /**
     * Проверяет пустой ли параметр
     *
     * @return {@code true}, если пустой
     */
    boolean isEmpty();

    /**
     *
     * @param param название параметра
     * @return {@code true}, если параметры являются целыми числами
     */
    boolean isInt(String param);

    /**
     *
     * @param param название параметра
     * @return {@code true}, если первый параметр является целым числом
     */
    boolean isIntF(String param);
}
