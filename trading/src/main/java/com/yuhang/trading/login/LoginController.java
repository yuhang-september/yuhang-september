package com.yuhang.trading.login;

import com.yuhang.service.entity.account.Account;
import com.yuhang.trading.account.service.AccountService;
import com.yuhang.service.common.Constants;
import com.yuhang.service.common.JsonResult;
import com.yuhang.service.common.RedisService;
import com.yuhang.service.common.RuleException;
import com.yuhang.service.common.utils.DateUtil;
import com.yuhang.service.common.utils.SessionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Description:
 * The controller is to deal with login and register.
 * All the response body is {@link JsonResult}.
 * Hide the original error as far as possible.
 *
 * @author David
 * 2024/2/27
 */
@Tag(name = "Login Controller", description = "The controller is to register or login.")
@RestController
public class LoginController {
    @Resource
    AccountService accountService;

    @Resource
    RedisService redisService;

    /**
     * Handle the request of login.
     *
     * @param account it contains two parameters: name and password.
     * @return JsonResult
     */
    @PostMapping("/login")
    public JsonResult login(@RequestBody Account account) {
        return validate(account.getName(), account.getPassword());
    }

    /**
     * Handle the request of register.
     *
     * @param account it contains all parameters except for the id which will be generated by system.
     * @return JsonResult
     */
    @PostMapping("/register")
    public JsonResult register(@RequestBody Account account) throws RuleException {
        validate(account);
        accountService.saveAccount(account);
        return JsonResult.generateResult(JsonResult.SUCCESS);
    }

    /**
     * Validate the information of account about name, certification, birthday and profession.
     *
     * @param account the input account information
     */
    private void validate(Account account) throws RuleException {
        String message = null;
        //other validation methods
        if (existAccount(account.getCertificationType(), account.getCertificationNo())) {
            message = "You have registered an account, please login.";
        } else if (StringUtils.isBlank(account.getName())) {
            message = "Please input your full name.";
        } else if (StringUtils.isBlank(account.getCertificationNo()) || !redisService.existRedisDictionaryItemValue(Constants.CERTIFICATION_TYPE, account.getCertificationType())) {
            message = "Your certification type or number is wrong, please check them and resubmit.";
        } else if (account.getBirthday().after(DateUtil.getCurrentDate())) {
            message = "Please input a valid birthday";
        } else if (!redisService.existRedisDictionaryItemValue(Constants.PROFESSION, account.getProfession())) {
            message = "Please input a valid profession";
        }

        if (StringUtils.isNotBlank(message)) {
            throw new RuleException(JsonResult.ACCOUNT_ERROR, message);
        }
    }

    private boolean existAccount(String certificationType, String certificationNo) {
        return null != accountService.findByCertification(certificationType, certificationNo);
    }

    /**
     * Getting information from database using the name and password.
     * If the account is null, then the name or password is wrong.
     * Use redis to record the count of attempts of one customer, if the count over a limited number, then lock the account.
     *
     * @param name     customer's name
     * @param password the encoded password
     * @return JsonResult
     */
    private JsonResult validate(String name, String password) {
        HashMap<String, Object> result = new HashMap<>(2);
        Account account = accountService.findByNameAndPassword(name, password);
        String code = JsonResult.SUCCESS;
        String message = null;
        if (null == account) {
            code = JsonResult.ACCOUNT_ERROR;
            message = "Your name or password is wrong, please check your information and login again.";
        } else {
            //record the customer information in session
            SessionUtil.getSession().setAttribute("account", account);
        }
        return JsonResult.generateResult(code, message);
    }
}
