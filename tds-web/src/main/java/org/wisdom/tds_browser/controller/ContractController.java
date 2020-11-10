package org.wisdom.tds_browser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.bean.Contract;
import org.wisdom.tds_browser.result.APIResult;
import org.wisdom.tds_browser.service.CoreRepository;
import org.wisdom.tds_browser.tool.PageTool;

@RestController
public class ContractController {

    @Autowired
    private CoreRepository coreRepository;

    @GetMapping("/get_binary_by_abi")
    @ResponseBody
    public APIResult<String> getBinaryByABI(@RequestParam(value = "abi") String abi) {
        return APIResult.newSuccess(coreRepository.getBinaryByABI(abi));
    }

    @GetMapping("/get_contract_list")
    @ResponseBody
    public APIResult<Page<Contract>> getContractList(@RequestParam(value = "per_page") Integer perPage,
                                                     @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(PageTool.getPageList(coreRepository.getContractList(), page, perPage));
    }

    @GetMapping("/get_call_contract_list")
    @ResponseBody
    public APIResult<Page<Block.Transaction>> getCallContractList(@RequestParam(value = "address") String address,
                                                                  @RequestParam(value = "per_page") Integer perPage,
                                                                  @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(PageTool.getPageList(coreRepository.getCallContractList(address), page, perPage));
    }

}
