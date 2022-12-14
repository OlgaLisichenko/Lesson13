import java.io.*;
import java.nio.file.*;
import java.util.*;
import static java.nio.file.StandardOpenOption.*;

/**
 * Программа должна получать имена файлов с номерами документов с консоли:
 * каждая новая строка - это путь к файлу и имя файла.
 * Для завершения ввода списка файлов следует ввести 0.
 * <p>После получения списка документов программа должна обработать каждый документ:
 * вычитать из файла номера документов и провалидировать их.
 * В конце работы создать один файл отчет с выходной информаций:
 * номер документа - комментарий(валиден или не валиден и по какой причине).
 * <p>Пусть каждый файл содержит каждый номер документа с новой строки и
 * в строке никакой другой информации, только номер документа.
 * <p>Валидный номер документа должен иметь длину 15 символов и начинаться с последовательности docnum
 * или contract(далее любая последовательность букв/цифр).
 * <p>Учесть, что номера документов могут повторяться в пределах одного файла
 * и так же разные документы могут содержать одни и те же номера документов.
 * Если номера документов повторяются, то повторные номера документов не проверять, не валидировать.
 *
 * <p>Немного технических деталей:
 * <p>1) Считать с консоли список документов: раз список - то это коллекции типа List,
 * никаких других условий нет - значит все имена файлов с консоли сохраняем в структуру данных ArrayList.
 * <p>2) Номера документов могут повторяться, но повторные документы обрабатывать не надо и валидировать не надо,
 * т.е. по сути дубликаты нам не нужны - значит надо считать номера документов из файлов
 * и все номера документов сохранять в коллекцию типа Set. Можно использовать HashSet.
 * <p>3) В конце должна быть структура: номер документа - комментарий,
 * т.е. эта структура типа ключ-значение, значит это коллекция типа Map. Можно использовать HashMap.
 * Создать такую структуру и уже потом сделать цикл по этой структуре
 * и записать всю информацию из этой мапы в файл-отчет.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("Set the file paths. To finish entering the list of files, enter 0");

            List<String> allPaths = getListOfPaths(scanner);
            Set<String> setStrings = getHashSetOfStrings(allPaths);
            Map<String, String> checkedDocs = getHashMapForReport(setStrings);
            writeDocNumsToReport(checkedDocs);
        }
    }

    private static List<String> getListOfPaths(Scanner scanner) {
        List<String> allPaths = new ArrayList<>();

        while (true) {
            String filePath = scanner.nextLine();
            if (filePath.equals("0")) break;
            allPaths.add(filePath);
        }
        return allPaths;
    }

    private static Set<String> getHashSetOfStrings(List<String> allPaths) throws IOException {
        List<String> numbersOfDoc = new ArrayList<>();

        for (String file : allPaths) {
            numbersOfDoc = Files.readAllLines(Paths.get(file));
        }
        return new HashSet<>(numbersOfDoc);
    }

    private static Map<String, String> getHashMapForReport(Set<String> setStrings) {
        Map<String, String> checkedDocs = new HashMap<>();

        for (String docNumber : setStrings) {
            if (isValidNumber(docNumber)) {
                checkedDocs.put(docNumber, "Valid document number");
            } else if (docNumber.length() != 15) {
                checkedDocs.put(docNumber, "Does not contain fifteen characters");
            } else if (!docNumber.startsWith("docnum") && !docNumber.startsWith("contract")) {
                checkedDocs.put(docNumber, "Does not start with docnum and with contract");
            } else {
                checkedDocs.put(docNumber, "Does not contain only litters or digits");
            }
        }
        return checkedDocs;
    }

    private static void writeDocNumsToReport(Map<String, String> checkedDocs) throws IOException {
        Path fileReport = Paths.get("resources", "checkedDocNums.txt");

        for (Map.Entry<String, String> entry : checkedDocs.entrySet()) {
            Files.write(fileReport, (entry.getKey() + " - " + entry.getValue() + "\n").getBytes(), CREATE, APPEND);
        }
        System.out.println("The path to the report file: " + fileReport.normalize());
    }

    private static boolean isValidNumber(String docNum) {
        boolean isDocNumberValid = (docNum.length() == 15 && (docNum.startsWith("docnum") || docNum.startsWith("contract")));
        char[] numberFromChars = docNum.toCharArray();
        boolean isContainOnlyLetterOrDigit = true;

        for (char ch : numberFromChars) {
            if (!Character.isLetterOrDigit(ch)) {
                isContainOnlyLetterOrDigit = false;
            }
        }
        return isContainOnlyLetterOrDigit && isDocNumberValid;
    }
}