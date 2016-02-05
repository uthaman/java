import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.Predicate;


public class FilterPredicate implements Predicate {

    private Object expected;
    private String propertyName;

    public FilterPredicate(String propertyName, Object expected) {
        super();
        this.propertyName = propertyName;
        this.expected = expected;
    }

    @Override
    public boolean evaluate(Object object) {
        try {
            Object value = PropertyUtils.getProperty(object, propertyName);
            if(null != value) {
                if(value instanceof Date) {
                    Date dateVal = (Date)value;
                    Date exVal = Utility.convertStringToDate((String)expected);
                    if(null != exVal) {
                        return dateVal.compareTo(exVal) == 0 ? true : false;
                    } else {
                        return false;
                    }
                } else {
                    String val = value.toString();
                    return val.toLowerCase().contains(expected.toString().toLowerCase());
                }
            } else {
                return false;
            }
        } catch(Exception e) {
            return false;
        }
    }
}
