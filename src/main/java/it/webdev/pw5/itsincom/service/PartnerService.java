package it.webdev.pw5.itsincom.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import io.quarkus.qute.Expression;
import it.webdev.pw5.itsincom.percistence.model.User;
import it.webdev.pw5.itsincom.percistence.repository.PartnerRepository;
import it.webdev.pw5.itsincom.percistence.model.Partner;
import it.webdev.pw5.itsincom.rest.model.PartnerRequest;
import it.webdev.pw5.itsincom.service.exception.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@ApplicationScoped
public class PartnerService {

    private static final String DB_NAME = "varese_dev";
    private static final String BUCKET_NAME = "partnerBucket";

    private final PartnerRepository partnerRepository;
    private final UserService userService;
    private final MongoClient mongoClient;

    public PartnerService(PartnerRepository partnerRepository, UserService userService, MongoClient mongoClient){
        this.partnerRepository = partnerRepository;
        this.userService = userService;
        this.mongoClient = mongoClient;
    }

    public List<Partner> getAllPartners(){
        return partnerRepository.getAllPartners();
    }

    public byte[] getPartnerImage(ObjectId imageId){
        GridFSBucket mongoBucket = GridFSBuckets.create(mongoClient.getDatabase(DB_NAME), BUCKET_NAME);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mongoBucket.downloadToStream(imageId, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void createPartner(String token, PartnerRequest p) throws UserNotFound, SessionNotFound, UserUnauthorized, PartnerAlreadyExists, XSSAttackAttempt {
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

        ObjectId imageId = saveImage(p.getName(), p.getImageBase64());

        Partner newPartner = new Partner();
        newPartner.setName(p.getName());
        newPartner.setDescription(p.getDescription());
        newPartner.setNumber(p.getNumber());
        newPartner.setLocation(p.getLocation());
        newPartner.setImageId(imageId);
        newPartner.setWebsiteURL(p.getWebsiteURL());

        partnerRepository.createPartner(newPartner);
    }

    private boolean partnerAlreadyExists(String name, String number, String location){
        Partner p = partnerRepository.checkDuplicates(name, number, location);
        return p != null;
    }

    private ObjectId saveImage(String name, String base64Image){
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        GridFSBucket mongoBucket = GridFSBuckets.create(mongoClient.getDatabase(DB_NAME), BUCKET_NAME);
        GridFSUploadOptions bucketSettings = new GridFSUploadOptions();

        ByteArrayInputStream imageInputStream = new ByteArrayInputStream(imageBytes);
        return mongoBucket.uploadFromStream(name, imageInputStream, bucketSettings);
    }
}
