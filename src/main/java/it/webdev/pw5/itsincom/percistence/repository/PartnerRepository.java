package it.webdev.pw5.itsincom.percistence.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.webdev.pw5.itsincom.percistence.model.Partner;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;


@ApplicationScoped
public class PartnerRepository implements PanacheMongoRepository<Partner> {

    public List<Partner> getAllPartners(){
        return listAll();
    }

    public void createPartner(Partner p){
        persist(p);
    }

    public Partner checkDuplicates(String name, String number, String location){
        return find("name = ?1 AND number = ?2 AND location = ?3", name, number, location).firstResult();
    }
}