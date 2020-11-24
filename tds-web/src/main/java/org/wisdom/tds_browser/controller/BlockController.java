package org.wisdom.tds_browser.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.result.APIResult;
import org.wisdom.tds_browser.service.CoreRepository;
import org.wisdom.tds_browser.tool.PageTool;

@RestController
public class BlockController {

    @Autowired
    private CoreRepository coreRepository;

    @GetMapping("/get_block_by_height")
    @ResponseBody
    public APIResult<Block> getBlockByHeight(@RequestParam(value = "height") int height) {
        return APIResult.newSuccess(coreRepository.getBlockByHeight(height));
    }

    @GetMapping("/get_transaction_by_hash")
    @ResponseBody
    public APIResult<Block.Transaction> getTransactionByTxHash(@RequestParam(value = "tx_hash") String txHash) {
        return APIResult.newSuccess(coreRepository.getTransactionByTxHash(txHash));
    }

    @GetMapping("/get_transaction_by_from")
    @ResponseBody
    public APIResult<Page<Block.Transaction>> getTransactionByFrom(@RequestParam(value = "from") String from,
                                                                   @RequestParam(value = "per_page") Integer perPage,
                                                                   @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(PageTool.getPageList(coreRepository.getTransactionByFrom(from), page, perPage));
    }

    @GetMapping("/get_transaction_by_to")
    @ResponseBody
    public APIResult<Page<Block.Transaction>> getTransactionByTo(@RequestParam(value = "to") String to,
                                                                 @RequestParam(value = "per_page") Integer perPage,
                                                                 @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(PageTool.getPageList(coreRepository.getTransactionByTo(to), page, perPage));
    }

    @GetMapping("/get_average rate")
    @ResponseBody
    public APIResult<Double> getAverageRate() {
        return APIResult.newSuccess(coreRepository.getAverageRate());
    }

    @GetMapping("/get_block_by_hash")
    @ResponseBody
    public APIResult<Block> getBlockByHash(@RequestParam(value = "block_hash") String blockHash) {
        return APIResult.newSuccess(coreRepository.getBlockByHash(blockHash));
    }

    @GetMapping("/get_transaction_list")
    @ResponseBody
    public APIResult<Page<Block.Transaction>> getTransactionList(@RequestParam(value = "per_page") Integer perPage,
                                                                 @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(coreRepository.getTransactionList(PageRequest.of(page, perPage, Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/get_block_list")
    @ResponseBody
    public APIResult<Page<Block>> getBlockList(@RequestParam(value = "per_page") Integer perPage,
                                               @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(coreRepository.getBlockList(PageRequest.of(page, perPage, Sort.Direction.DESC, "height")));
    }

    @GetMapping("/get_transaction_list_by_address")
    @ResponseBody
    public APIResult<Page<Block.Transaction>> getTransactionListByAddress(@RequestParam(value = "address") String address,
                                                                          @RequestParam(value = "per_page") Integer perPage,
                                                                          @RequestParam(value = "page") Integer page) {
        return APIResult.newSuccess(PageTool.getPageList(coreRepository.getTransactionListByAddress(address), page, perPage));
    }

    @GetMapping(value = "/stat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object stat(){
        return JSON.parse(coreRepository.stat());
    }

    @GetMapping(value = "/pool", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object pool(){
        return JSON.parse(coreRepository.pool());
    }

    @GetMapping(value = "/account/{addressOrPublicKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getAccount(@PathVariable String addressOrPublicKey) {
        return JSON.parse(coreRepository.getAccount(addressOrPublicKey));
    }

}
