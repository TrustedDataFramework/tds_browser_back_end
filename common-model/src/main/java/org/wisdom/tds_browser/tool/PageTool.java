package org.wisdom.tds_browser.tool;

import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

public class PageTool {

    private static <T> List<T> startPage(List<T> list, Integer pageIndex,
                                         Integer pageSize) {
        if (list == null) {
            return new ArrayList<>();
        }
        if (list.size() == 0) {
            return new ArrayList<>();
        }

        if (pageIndex == -1) {
            return list;
        }

        if (list.size() <= pageSize) {
            pageSize = list.size();
        }

        int count = list.size(); // 记录总数
        int pageCount; // 页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }

        int fromIndex; // 开始索引
        int toIndex; // 结束索引

        if (pageIndex * pageSize > count) {
            toIndex = count;
            fromIndex = count / pageSize * pageSize;
            return list.subList(fromIndex, toIndex);
        }

        if (pageIndex - 1 != pageCount) {
            fromIndex = pageIndex * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = pageIndex * pageSize;
            toIndex = count;
        }
        if (toIndex > count) {
            return list.subList(fromIndex, count);
        } else {
            return list.subList(fromIndex, toIndex);
        }

    }

    public static <T> Page<T> getPageList(List<T> list, Integer page,
                                          Integer perPage) {
        if (list == null) {
            list = new ArrayList<>();
        }

        List<T> pageList = startPage(list, page, perPage);
        if (page  > list.size() / perPage) {
            page = list.size() / perPage;
            pageList = new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(page, perPage, Sort.Direction.DESC, "pageable");
        return new PageImpl<>(pageList, pageable, list.size());
    }

}
