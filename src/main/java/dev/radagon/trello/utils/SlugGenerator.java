package dev.radagon.trello.utils;

import com.ibm.icu.text.Transliterator;
import org.springframework.stereotype.Component;

@Component
public class SlugGenerator {

    // Правило транслитерации из кириллицы в латиницу по стандарту
    private static final Transliterator CYRILLIC_TO_LATIN =
            Transliterator.getInstance("Russian-Latin/BGN; Any-Lower");

    public String generateSlug(String name) {
        if (name == null) {
            return "";
        }

        // 1. Переводим кириллицу в латиницу
        String latinText = CYRILLIC_TO_LATIN.transliterate(name);

        // 2. Очищаем строку по вашему алгоритму
        return latinText
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
