package uk.co.bconline.ndelius.util;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.experimental.UtilityClass;
import lombok.val;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.ExportResult;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.transformer.ColumnNameAndPositionCSVMappingStrategy;

import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;

@UtilityClass
public class CSVUtils {
    private static <T> StatefulBeanToCsv<T> getCsvWriter(Class<T> tClass, Writer writer) {
        ColumnNameAndPositionCSVMappingStrategy<T> mappingStrategy = new ColumnNameAndPositionCSVMappingStrategy<>();
        mappingStrategy.setType(tClass);
        return new StatefulBeanToCsvBuilder<T>(writer)
            .withMappingStrategy(mappingStrategy)
            .build();
    }

    public static void write(List<SearchResult> results, Writer writer)
        throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        getCsvWriter(SearchResult.class, writer).write(results);
    }

    public static void stream(Stream<ExportResult> results, Writer writer) {
        val csv = getCsvWriter(ExportResult.class, writer);
        results.forEachOrdered(result -> {
            try {
                csv.write(result);
            } catch (CsvException e) {
                throw new AppException(String.format("Failed to write CSV entry for %s", result.getUsername()), e);
            }
        });
    }
}
