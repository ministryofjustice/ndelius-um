package uk.co.bconline.ndelius.transformer;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.val;

import static uk.co.bconline.ndelius.util.NameUtils.camelCaseToTitleCase;

/**
 * @param <T>
 */
public class ColumnNameAndPositionCSVMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
    /*
     * (non-Javadoc)
     *
     * @see com.opencsv.bean.ColumnPositionMappingStrategy#generateHeader(java.lang.
     * Object)
     */
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        final int numColumns = getFieldMap().values().size();
        final String[] header = new String[numColumns];
        super.setColumnMapping(header);
        for (int i = 0; i < numColumns; i++) {
            val field = findField(i).getField();
            val annotation = field.getDeclaredAnnotation(CsvBindByName.class);
            if (annotation != null) {
                header[i] = annotation.column().trim();
            } else {
                // Fall back to field name if missing name annotation
                header[i] = camelCaseToTitleCase(field.getName());
            }
        }
        return header;
    }
}
