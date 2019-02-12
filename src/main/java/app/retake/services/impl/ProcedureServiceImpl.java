package app.retake.services.impl;

import app.retake.domain.dto.*;
import app.retake.domain.models.*;
import app.retake.parser.interfaces.ModelParser;
import app.retake.repositories.ProcedureRepository;
import app.retake.services.api.AnimalAidService;
import app.retake.services.api.AnimalService;
import app.retake.services.api.ProcedureService;
import app.retake.services.api.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final ModelParser modelParser;
    private final VetService vetService;
    private final AnimalService animalService;
    private final AnimalAidService animalAidService;

    @Autowired
    public ProcedureServiceImpl(ProcedureRepository procedureRepository, ModelParser modelParser,
                                VetService vetService, AnimalService animalService, AnimalAidService animalAidService) {
        this.procedureRepository = procedureRepository;
        this.modelParser = modelParser;
        this.vetService = vetService;
        this.animalService = animalService;
        this.animalAidService = animalAidService;
    }

    @Override
    public void create(ProcedureXMLImportDTO dto) {
        Animal animal =
                this.animalService.getByPassprtSerrialNumber(dto.getAnimal());
        Vet vet = this.vetService.getByName(dto.getVet());
        if (animal != null || vet != null){
           Set<AnimalAid> animalAid =
                   dto.getAnimalAids()
                   .stream().map(this::apply).collect(Collectors.toSet());

            Procedure procedure = new Procedure();
            procedure.setAnimal(animal);
            procedure.setVet(vet);
            procedure.setDatePerformed(dto.getDate());
            procedure.setServices(animalAid);
            this.procedureRepository.saveAndFlush(procedure);
        }else{
            throw new IllegalArgumentException();
        }

    }

    @Override
    public ProcedureXMLWrapperExportDTO exportProcedures() {
        List<Procedure> procedures = this.procedureRepository.findAll();

        List<ProcedureXMLExportDTO> export =
                procedures.stream()
                .map(p ->{
                    Passport passport = p.getAnimal().getPassport();

                    List<ProcedureAnimalAidXMLImportDTO> animalAids = p.getServices()
                            .stream()
                            .map(ai-> new ProcedureAnimalAidXMLImportDTO(ai.getName(),ai.getPrice()))
                            .collect(Collectors.toList());
                    ProcedureXMLExportDTO dto = new ProcedureXMLExportDTO(
                            passport.getOwnerPhoneNumber(),
                            passport.getSerialNumber(),
                            animalAids
                    );
                    return dto;
                })
                .collect(Collectors.toList());
        return new ProcedureXMLWrapperExportDTO(export);
    }

    private AnimalAid apply(ProcedureAnimalAidXMLImportDTO animalAid) {
        AnimalAid aid = this.animalAidService
                .findOneByName(animalAid.getName());
        if (aid == null) throw new IllegalArgumentException();
        return aid;
    }
}

