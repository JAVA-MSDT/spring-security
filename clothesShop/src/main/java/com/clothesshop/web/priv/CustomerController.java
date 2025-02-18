package com.clothesshop.web.priv;

import com.clothesshop.model.user.Customer;
import com.clothesshop.model.user.security.UserSecurity;
import com.clothesshop.service.CustomerService;
import com.clothesshop.util.ResourcesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    @Value("${users.images.folder}")
    private String userImagesFolder;

    private final CustomerService customerService;

    @GetMapping
    public String getAllCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("module", "customers");
        return "private/user/customers";
    }

    @GetMapping(path = "/{id}")
    public String getCustomerById(@PathVariable("id") long id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("userSecurity", customer.getUserSecurity());
        model.addAttribute("module", "customers");
        return "private/user/user_profile";
    }

    @GetMapping(path = "/{customerId}/remove-clothe/{id}")
    public String removeClotheFromMyList(@PathVariable(value = "id") long id,
                                         @PathVariable(value = "customerId") long customerId,
                                         RedirectAttributes redirectAttributes) {
        try {
            customerService.removeClotheFromCustomer(customerId, id);
            return "redirect:/profile#v-pills-clothe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("removeClothMessage", e.getLocalizedMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping(path = "/add-clothe/{id}")
    public String AddClotheToMyList(@PathVariable(value = "id") long id, @AuthenticationPrincipal UserSecurity userSecurity) {
        customerService.addClotheToCustomer(userSecurity.getCustomer().getId(), id);
        return "redirect:/profile#v-pills-clothe";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(value = "id") long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "DataIntegrityViolationException: Item with id " + id + " already used somewhere in the system");
            return "redirect:/customers";
        }
        redirectAttributes.addFlashAttribute("deleteMessage", "Deleted customer with id: " + id);
        return "redirect:/customers";
    }

    @PostMapping(path = "/update-customer-basic")
    public String updateCustomerBasicDetails(@ModelAttribute("customer") Customer customer) {
        customer = customerService.updateCustomerBasicDetails(customer);
        return "redirect:/user-security/account-settings/" + customer.getUserSecurity().getId() + "#v-pills-contact-info";
    }

    @PostMapping("/update-customer-profile-image")
    public String updateCustomerProfileImage(@RequestParam("id") Long id, @RequestParam("customerProfileImage") MultipartFile customerProfileImage) throws IOException {
        String imageName = ResourcesUtil.saveImageToFolder(customerProfileImage, userImagesFolder);
        String imageUrl = "/images/users/" + imageName;
        Customer customer = customerService.updateCustomerProfileImage(id, imageUrl);
        return "redirect:/user-security/account-settings/" + customer.getUserSecurity().getId() + "#v-pills-contact-info";
    }

    @PostMapping("/update-customer-banner-image")
    public String updateCustomerBannerImage(@RequestParam("id") Long id, @RequestParam("customerBannerImage") MultipartFile customerBannerImage) throws IOException {
        String imageName = ResourcesUtil.saveImageToFolder(customerBannerImage, userImagesFolder);
        String imageUrl = "/images/users/" + imageName;
        Customer customer = customerService.updateCustomerBannerImage(id, imageUrl);
        return "redirect:/user-security/account-settings/" + customer.getUserSecurity().getId() + "#v-pills-contact-info";
    }

}
