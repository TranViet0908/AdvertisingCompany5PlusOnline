package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Company;
import ProjectSpringboot.AdvertisingCompany.Repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public List<Company> getAllCompany(){
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id){
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
    }

    public List<Company> getCompanyByName(String name){
        return companyRepository.findCompanyByName(name);
    }

    public Company createCompany(Company company){
        return companyRepository.save(company);
    }

    public Company updateCompany(Long id, Company companyDetails){
        Company company = getCompanyById(id);
        company.setAddress(companyDetails.getAddress());
        company.setPhone(companyDetails.getPhone());
        company.setEmail(companyDetails.getEmail());
        company.setWebsite(companyDetails.getWebsite());
        company.setCreated_at(companyDetails.getCreated_at());
        company.setUpdated_at(companyDetails.getUpdated_at());
        return companyRepository.save(company);
    }

    public void deleteCompany(Long id){
        Company company = getCompanyById(id);
        companyRepository.delete(company);
    }
    public long countCompanies() {
        return companyRepository.count();
    }
}
