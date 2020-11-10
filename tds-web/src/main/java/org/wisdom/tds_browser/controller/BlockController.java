package org.wisdom.tds_browser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.tds_browser.bean.Block;
import org.wisdom.tds_browser.service.CoreRepository;
import org.wisdom.tds_browser.tool.PageTool;

@RestController
public class BlockController {

    @Autowired
    private CoreRepository coreRepository;

    @GetMapping("/get_block_by_height")
    @ResponseBody
    public Block getBlockByHeight(@RequestParam(value = "height") int height) {
        return coreRepository.getBlockByHeight(height);
    }

    @GetMapping("/get_transaction_by_hash")
    @ResponseBody
    public Block.Transaction getTransactionByTxHash(@RequestParam(value = "tx_hash") String txHash) {
        return coreRepository.getTransactionByTxHash(txHash);
    }

    @GetMapping("/get_transaction_by_from")
    @ResponseBody
    public Page<Block.Transaction> getTransactionByFrom(@RequestParam(value = "from") String from,
                                                        @RequestParam(value = "per_page") Integer perPage,
                                                        @RequestParam(value = "page") Integer page) {
        return PageTool.getPageList(coreRepository.getTransactionByFrom(from), page, perPage);
    }

    @GetMapping("/get_transaction_by_to")
    @ResponseBody
    public Page<Block.Transaction> getTransactionByTo(@RequestParam(value = "to") String to,
                                                      @RequestParam(value = "per_page") Integer perPage,
                                                      @RequestParam(value = "page") Integer page) {
        return PageTool.getPageList(coreRepository.getTransactionByTo(to), page, perPage);
    }

}
