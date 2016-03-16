package library.singularity.com.data.model.mappers;

import com.parse.ParseObject;

import library.singularity.com.data.model.DiscountCode;
import library.singularity.com.data.utils.Constants;

public class DiscountCodeEntityMapper {

    public static DiscountCode convertToDiscountCode(ParseObject parseCode){
        if(parseCode == null) return null;

        DiscountCode code = new DiscountCode();
        code.setId(parseCode.getObjectId());
        code.setCode(parseCode.getString(Constants.PARSE_DISCOUNT_CODE_CODE));
        code.setAmount(parseCode.getDouble(Constants.PARSE_DISCOUNT_CODE_AMOUNT));
        code.setIsPercentage(parseCode.getBoolean(Constants.PARSE_DISCOUNT_CODE_IS_PERCENTAGE));

        return code;
    }

    public static ParseObject convertToParseObject(DiscountCode code){
        if( code == null) return null;

        ParseObject codeParseObject = new ParseObject(Constants.PARSE_DISCOUNT_CODE_TABLE_NAME);

        if(code.getId() != null){
            codeParseObject.put(Constants.PARSE_DISCOUNT_CODE_ID,code.getId());
        }

        codeParseObject.put(Constants.PARSE_DISCOUNT_CODE_CODE,code.getCode());
        codeParseObject.put(Constants.PARSE_DISCOUNT_CODE_AMOUNT,code.getAmount());
        codeParseObject.put(Constants.PARSE_DISCOUNT_CODE_IS_PERCENTAGE,code.isPercentage());


        return codeParseObject;
    }
}
