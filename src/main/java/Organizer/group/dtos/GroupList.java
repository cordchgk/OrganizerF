package organizer.group.dtos;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "groups")
public class GroupList {
    private List<GroupDTO> group;

    @XmlElement(name = "group")
    public List<GroupDTO> getGroup() {
        return group;
    }

    public void setGroup(List<GroupDTO> group) {
        this.group = group;
    }
}
