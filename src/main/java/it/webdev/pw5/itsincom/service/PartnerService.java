package it.webdev.pw5.itsincom.service;

import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.PartnerRepository;
import it.webdev.pw5.itsincom.percistence.model.Partner;
import it.webdev.pw5.itsincom.rest.model.PartnerRequest;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;


@ApplicationScoped
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserService userService;

    public PartnerService(PartnerRepository partnerRepository, UserService userService){
        this.partnerRepository = partnerRepository;
        this.userService = userService;
    }

    public List<Partner> getAllPartners(){
        return partnerRepository.getAllPartners();
    }

    public void addPartner(String token, PartnerRequest p) throws UserNotFound, SessionNotFound, UserUnauthorized, PartnerAlreadyExists, XSSAttackAttempt {
        p.sanitizer();
        User user = userService.findUserByToken(token);
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new UserUnauthorized();
        }
        // Check name AND number AND location for duplicates
        boolean partnerIsDuplicated = partnerAlreadyExists(p.getName(), p.getNumber(), p.getLocation());
        if (partnerIsDuplicated){
            throw new PartnerAlreadyExists();
        }

        Partner newPartner = new Partner();
        newPartner.setName(p.getName());
        newPartner.setDescription(p.getDescription());
        newPartner.setNumber(p.getNumber());
        newPartner.setLocation(p.getLocation());
        newPartner.setImageBase64(p.getImageBase64());
        newPartner.setWebsiteURL(p.getWebsiteURL());
        partnerRepository.addPartner(newPartner);
    }

    private boolean partnerAlreadyExists(String name, String number, String location){
        Partner p = partnerRepository.checkDuplicates(name, number, location);
        return p != null;
    }
}
