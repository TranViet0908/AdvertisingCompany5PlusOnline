package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.Partner;
import ProjectSpringboot.AdvertisingCompany.Repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService {
    @Autowired
    private PartnerRepository partnerRepository;

    public List<Partner> getAllPartner(){
        return partnerRepository.findAll();
    }

    public Partner getPartnerById(Long id){
        return partnerRepository.findById(id).orElseThrow(() -> new RuntimeException("Partner not found by id" + id));
    }

    public List<Partner> getPartnerByName(String name){
        return partnerRepository.getPartnerByName(name);
    }

    public Partner createPartner(Partner partner){
        return partnerRepository.save(partner);
    }

    public Partner updatePartner(Long id, Partner partnerDetails){
        Partner partner = getPartnerById(id);
        partner.setName(partnerDetails.getName());
        partner.setEmail(partnerDetails.getEmail());
        partner.setPhone(partnerDetails.getPhone());
        partner.setAddress(partnerDetails.getAddress());
        partner.setCompany(partnerDetails.getCompany());
        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id){
        Partner partner = getPartnerById(id);
        partnerRepository.delete(partner);
    }

    public long countPartners() {
        return partnerRepository.count();
    }
}
