package library.singularity.com.dao.database.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;

import library.singularity.com.dao.database.DatabaseMetaData;
import library.singularity.com.data.model.DiscountCode;

/**
 * Created by XPC on 8/10/2015.
 */
public class DiscountCodeDaoMapper {

    public static HashMap<String,Integer> NAME_INDEX_MAP = new HashMap<>();

    public static void mapCursor(Cursor cursor){
        int n = cursor.getColumnCount();
        for (int i = 0; i < n; i++){
            NAME_INDEX_MAP.put(cursor.getColumnName(i),i);
        }
    }

    public static DiscountCode convertCursorToDiscountCode(Cursor cursor){
        DiscountCode discountCode = new DiscountCode();

        Integer columnIndex = getColumnIndex(DatabaseMetaData.DiscountCodeTableMetaData.ID);
        if(columnIndex != null){
            discountCode.setId(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.DiscountCodeTableMetaData.CODE);
        if(columnIndex != null){
            discountCode.setCode(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.DiscountCodeTableMetaData.AMOUNT);
        if(columnIndex != null){
            discountCode.setAmount(cursor.getDouble(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.DiscountCodeTableMetaData.IS_PERCENTAGE);
        if(columnIndex != null){
            discountCode.setIsPercentage(cursor.getInt(columnIndex) == 1);
        }
        return discountCode;
    }

    public static Integer getColumnIndex(String columnName){
        return NAME_INDEX_MAP.get(columnName);
    }

    public static ContentValues getDiscountCodeFromItems(DiscountCode discountCode){
        ContentValues values = new ContentValues();

        values.put(DatabaseMetaData.DiscountCodeTableMetaData.ID,discountCode.getId());
        values.put(DatabaseMetaData.DiscountCodeTableMetaData.CODE,discountCode.getCode());
        values.put(DatabaseMetaData.DiscountCodeTableMetaData.AMOUNT,discountCode.getAmount());
        values.put(DatabaseMetaData.DiscountCodeTableMetaData.IS_PERCENTAGE,discountCode.isPercentage() ? 1 : 0);

        return values;
    }

}
