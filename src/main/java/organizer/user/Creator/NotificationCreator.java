package organizer.user.Creator;

import organizer.user.dtos.NotificationDTO;

public class NotificationCreator {


    public String newProductNotification(NotificationDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("New Product ");
        sb.append(dto.getName());
        sb.append(" was created!");


        return sb.toString();
    }


}
