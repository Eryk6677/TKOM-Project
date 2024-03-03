# Projekt TKOM:
## TYPOWANIE - Statyczne/Silne
- Typ zmiennej jest określany podczas tworzenia tej zmiennej i jest niezmienny przez cały czas jej życia.
- Typy zmiennych są ściśle określone i użycie zmiennej w kontekście innego typu wymaga skonwertowania jej (zazwyczaj jawnie) do innego typu. Operacje są zdefiniowane dla konkretnych zestawów typów, np. niemożliwe jest dodanie do siebie stringa i liczby, jeśli język jawnie nie dopuszcza takiej możliwości.

## MUTOWALNOŚĆ - Domyślnie stałe
- Zmienne są domyślnie stałymi - wartość można im przypisać wyłącznie raz (np. podczas inicjalizacji). Jeżeli chcemy móc wartości przypisywać wielokrotnie, zmienną trzeba oznaczyć jako mutowalną (domyślnie wartość jest CONST, czyli jak przypiszę jej wartość, to nie da się jej już później zmienić! int stala = 7, ALE jeśli dodam na paczątku słowo kluczowe mut(mutowalne) to staje się już zmienną mutowalną!)

## OPCJONALNOŚĆ - Opcjonalne
- Zmienne mogą być nullami. Zmienne mogą mieć stan oznaczający, że nie zawierają żadnej wartości, np. `int? x = {}` oznacza, że `x` jest typu `int` ale może nie mieć wartości (`?`) i przypisany jest do niego jawnie brak wartości (`{}`). Dla zmiennych tej kategorii powinny być zdefiniowane operatory, które ułatwiają korzystanie z nich, np. wykonanie jakiejś operacji pod warunkiem, że zmienna ma wartość. Przykład ze słowem klucz "otherwise" (var someCat = getCat() then addBowTie then crop otherwise getDefaultCat),

## PRZEKAZYWANIE - Domyślnie przez wartość
- Do funkcji domyślnie przekazywana jest kopia zmiennej. Modyfikacja jej wartości w funkcji nie wpływa na wartość zmiennej, która była przekazana jako argument wywołania. Wynik funkcji również zwracany domyślnie przez wartość. Jeżeli chcemy przekazać zmienną przez referencję, należy to w jakiś sposób oznaczyć ("ref", "&", itd.).

## PATTERN MATCHING
- Przykład w Rust (typowanie statyczne, silne, niemutowalne, opcjonalne)
- https://doc.rust-lang.org/book/ch18-03-pattern-syntax.html

## Podstawowe założenia:
- Projekt zostanie zrealizowany z wykorzystaniem języka Java.
- Efekty pracy będą stopniowo umieszczane w repozytorium wydziałowym GitLab.
- Jako środowisko robocze wykorzystam IntelliJ IDEA.

## Zasady działania języka:
1. Obsługa podstawowych typów liczbowych (int oraz float).
2. Typy liczbowe będą obsługiwać podstawowe operacje liczbowe `(+, -, *, /, %)`. Dopuszczalne będą jedynie operacje wykonywane na zmiennych tego samego typu, jednakże dostępny będzie też mechanizm jawnego castowania typów zmiennych, umożliwiający wykonywanie przykładowo operacji sumowania dwóch liczb. Zakładam, że operacje otrzymując na wejściu zmienne o wartościach całkowitoliczbowych, zwrócą również wartość całkowitoliczbową i podobnie w przypadku liczb niecałkowitych. Wyjątkiem będzie operator dzielenia, który zawsze będzie zwracał wartość w postaci liczby zmiennoprzecinkowej (float).
3. Język będzie posiadał zmienne typu bool.
4. Obsługiwane będą również operatory logiczne `(<, >, <=, >=, ==, !=, &&, ||)`. Operatory porównania będą współpracować z typami liczbowymi oraz typami boolowskimi.
5. Wspomniane operacje będą oczywiście obsługiwać priorytety wykonywania (tak jak w C++) oraz obsługiwać nawiasowanie.
6. Obsługa typu znakowego String.
7. String będzie obsługiwać konkatenację wyrażaną `+` i zwracającą wynikowy string. `string out = str1 + str2`
8. Chcąc połączyć zmienną typu znakowego oraz innego, należy jawnie dokonać castowania do typu string! Typ boolowski i liczbowe można będzie przekształcić bezproblemowo do postaci znakowej, jednakże w drugą stronę wymagane będzie ponowne sparsowanie liczby z ciągu znaków, bool nie będzie problemem.
9. Obsługa escaping characters — dodanie backslash przed znakiem powoduje, że jest on odpowiednio interpretowany. `string str1 = "inner quote \""`
10. Obsługa komentarzy — Komentarz będzie rozpoczynał się znakiem `#` a jego koniec będzie równoznaczny z końcem linii.
11. Bloki instrukcyjne muszą być zawarte w klamrach! `{... code ...}`.
12. Zawiera instrukcję pętli `while` wzorowaną na języku C++.
13. Zawiera instrukcję warunkową `if ... else` wzorowaną na języku C++.
14. Język przewiduje możliwość definiowania własnych zmiennych w formacie `(mut) TYP_ZMIENNEJ(?) NAZWA_ZMIENNEJ = WARTOŚĆ_ZMIENNEJ`, gdzie:
    - `(mut)` to opcjonalne słowo kluczowe decydujące o tym, czy zmienna ma być mutowalna (jej wartość może się zmieniać przez czas jej istnienia),
    - `TYP_ZMIENNEJ` to jeden z określonych typów zmiennych `(int, float, string, bool)`,
    - `(?)` język obsługuje opcjonalność zmiennych, w przypadku zmiennej, która nie musi mieć przypisanej wartości, należy zaznaczyć, że zmienna ta może mieć wartość null, jest ona wtedy obsługiwana bezpiecznymi wariantami funkcji.
    - `NAZWA_ZMIENNEJ` to przypisana zmiennej nazwa, przy pomocy której będziemy się do niej odwoływać (musi zaczynać się małą literą).
    - `WARTOŚĆ ZMIENNEJ` to oczywiście wartość, którą przypisujemy danej zmiennej, zgodna z wcześniej ustalonym typem.
15. Język umożliwia użytkownikowi definiowanie własnych funkcji w formacie: `def TYP_ZWRACANEJ_WARTOŚCI NAZWA_FUNKCJI(PARAMETRY) { BLOK_INSTRUKCJI }`, gdzie:
    - `def` słowo kluczowe informujące, że w tym miejscu zdefiniowana jest funkcja.
    - `TYP_ZWRACANEJ_WARTOŚCI` określenie jakiego typu wartość zwraca dana funkcja (bądź też nie zwraca nic) `(void, int, float, string, bool)`.
    - `NAZWA_FUNKCJI` to przypisana funkcji nazwa, której należy używać, chcąc skorzystać z funkcji.
    - `(PARAMETRY)` to zawarte w nawiasach i oddzielone przecinkami, przyjmowane w funkcji parametry zapisywane w postaci: `TYP_PARAMETRU NAZWA_PARAMETRU`.
    - `{ BLOK_INSTRUKCJI }` to zapisany w klamrach zestaw instrukcji, jakie powinna wykonywać zdefiniowana funkcja.
16. Język domyślnie przekazuje przez wartość, tj. przekazywana jest kopia zmiennej, a jej wartość pozostaje niezmieniona.
17. Język ma oczywiście wbudowany system obsługi błędów, który w sposób czytelny informuje użytkownika gdzie i jaki błąd napotkał.
18. PATTERN MATCHING:
    - Obsługuje on typ liczbowy,
    - Umożliwia tworzenie zestawów porównań, korzystając z operatorów porównań `(<, >, <=, >=, ==, !=)`, wartość porównywana zawsze znajduje się po LEWEJ stronie operatora,
    - Możliwe jest wykorzystywanie tworzonych przez użytkownika predykatów,
    - Predykaty są to jednoargumentowe funkcje, których typem zwracanej wartości jest bool,
    - Zarówno predykaty, jak i zmienne podawane są poprzez podanie tylko ich identyfikatora, kompilator sam rozróżnia czy wykorzystywana jest wartość zmiennej, czy wołana jest funkcja.
    - Podane na wejściu wyrażenie jest ewaluowane i zapisywane pod postacią wewnętrznej zmiennej `val`, (to słowo kluczowe jest zarezerwowane i nie ma możliwości wykorzystywać go w kodzie!)
    - Po dopasowaniu wykonywany jest blok instrukcyjny przypisany do danego case'a.
19. Główny zestaw instrukcji do wykonania musi być umieszczony w specjalnej funkcji `def void main() {}`.
20. Wszelkie funkcje muszą być definiowane w scopie globalnym, nie ma możliwości zagnieżdżania funkcji.

## Przykłady wykorzystania:
By skorzystać z języka, wystarczy podać jako argument ścieżkę do docelowego pliku.
Przykłady konstrukcji językowych obrazujących wyżej opisaną funkcjonalność znajdują się w folderze `test_files`.

## Struktura projektu:
- Klasa `App.java` która będzie bazą całego projektu, tutaj będzie można podać krótki fragment kodu bezpośrednio w postaci String, albo podać ścieżkę do pliku zawierając docelowy kod źródłowy.
- Klasa `Lexer.java`, która zawierać będzie implementację analizatora leksykalnego, który leniwie, wczytując kolejno znaki ze źródła, będzie w stanie generować ciąg tokenów przekazywanych na bieżąco do kolejnej części.
- Klasa `Parser.java`, która odpowiedzialna będzie za parsowanie przekazywanych z lexera tokenów, a następnie budowanie gotowej struktury obiektów przekazywanej do wykonania.
- Klasa `Interpreter.java`, która będzie finalną częścią projektu, która po zakończeniu procedury analizy składniowej przeprowadzi proces interpretacji wygenerowanej struktury obiektów.
- Paczka `Exceptions`, jak wskazuje nazwa, zawiera wszystkie możliwe błędy, jakie mogą wystąpić w trakcie procedury analizy leksykalnej, składniowej oraz finalnie interpretacji.
- Paczka `Tokens`, zawiera wszystkie potrzebne elementy do przekształcenia wejścia na przygotowane do dalszej analizy Tokeny. Jest tu również interface `ReturnValue`, umożliwiający przechowywanie wartości danego tokenu zachowując od razu poprawnie zinterpretowany typ (liczba/napis).
- Paczka `Nodes`, zawiera wszystkie potrzebne elementy do zbudowania struktury obiektów, która to później zostanie poddana interpretacji. Znajdują się klasy odpowiedzialne za budowanie wszystkich możliwych wyrażeń, statementów oraz pozostałych niezbędnych elementów.
- Paczka `Scope`, przechowuje w sobie tylko klasę `Context`, czyli kontekst aktualnie wykonywanej funkcji, oraz Reader używany głównie w testach, by móc wygodnie pozyskiwać wynik operacji.

## Gramatyka:
Pełny i zweryfikowany opis gramatyki znajduje się w pliku `ebnf`.

## Testowanie:
Projekt zawiera 3 klasy, w których umieszczone są testy.
1. `LexerTest` - 86 testów sprawdzających, czy analizator leksykalny poprawnie przekształca kod na Tokeny.
2. `ParserTest` - 104 testy sprawdzające, czy analizator składniowy poprawnie rozpoznaje wyrażenia i na podstawie przekazywanych do niego Tokenów buduje poprawną strukturę obiektów.
3. `InterpreterTest` - 68 testów sprawdzających, czy interpreter finalnie poprawnie wykonuje różne typowe konstrukcje, jakie można napisać w języku. Są one w strukturze E2E, ponieważ wykonują one pełny proces — od analizy leksykalnej, aż po interpretację.