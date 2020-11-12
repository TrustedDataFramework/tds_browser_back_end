package org.wisdom.tds_browser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.bean.Contract;
import org.wisdom.tds_browser.bean.Pair;
import org.wisdom.tds_browser.result.APIResult;
import org.wisdom.tds_browser.service.CoreRepository;
import org.wisdom.tds_browser.tool.PageTool;

import java.io.IOException;

@RestController
public class ContractController {

    @Autowired
    private CoreRepository coreRepository;

    @GetMapping("/get_binary_by_abi")
    @ResponseBody
    public APIResult<String> getBinaryByABI(@RequestParam(value = "abi") String abi) {
        return APIResult.newSuccess(coreRepository.getBinaryByABI(abi));
    }

    @GetMapping("/get_abi_by_address")
    @ResponseBody
    public APIResult<String> getABIByAddress(@RequestParam(value = "address") String address) {
        Pair<Boolean, String> pair = coreRepository.getABIByAddress(address);
        if (pair.key) {
            return APIResult.newSuccess(pair.value);
        }
        return APIResult.newFailed(pair.value);
    }

    @RequestMapping(value = "/upload_contract_code", method = RequestMethod.POST)
    @ResponseBody
    public APIResult<String> uploadContractCode(@RequestPart("upload_file") MultipartFile uploadFile,
                                                @RequestParam(value = "address") String address) throws IOException {
        Pair<Boolean, String> pair = coreRepository.uploadContractCode(uploadFile, address);
        if (pair.key) {
            return APIResult.newSuccess(pair.value);
        }
        return APIResult.newFailed(pair.value);
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
