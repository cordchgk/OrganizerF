package organizer.validator;


import organizer.shopping.list.beans.ShoppingListBean;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@RequestScoped
@Named("validationBean")
public class ValidationBean implements Serializable {


    @Inject
    ShoppingListBean s_L_Bean;

    public void validateSearch(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        /**
         String w = (String) value;
         for (char c : w.toCharArray()) {
         if (!Character.isLetter(c)) {
         throw new ValidatorException(new FacesMessage("Value is invalid!"));
         }
         }
         **/

    }

    public void validateIngredientInput(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String s = (String) value;
        if (!s.matches("[a-zA-Z]+")) {

            throw new ValidatorException(new FacesMessage("Value is invalid!"));
        }

    }


    public void validateDate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        LocalDate start = this.s_L_Bean.getS_D();
        LocalDate d = (LocalDate) value;
        if (d.isBefore(start)) {
            throw new ValidatorException(new FacesMessage("Date is before today!"));
        }

    }
}


