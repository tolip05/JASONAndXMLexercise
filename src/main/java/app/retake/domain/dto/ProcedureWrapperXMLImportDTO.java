package app.retake.domain.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "procedures")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcedureWrapperXMLImportDTO {
    @XmlElement(name = "procedure")
    @XmlElementWrapper
    private List<ProcedureXMLImportDTO> procedures;

    public ProcedureWrapperXMLImportDTO() {
    }

    public List<ProcedureXMLImportDTO> getProcedures() {
        return this.procedures;
    }

    public void setProcedures(List<ProcedureXMLImportDTO> procedures) {
        this.procedures = procedures;
    }
}
