package com.mjc.school.service.filter.mapper;

import com.mjc.school.repository.filter.pagination.Pagination;
import com.mjc.school.repository.filter.sorting.SortOrder;
import com.mjc.school.repository.filter.sorting.Sorting;
import com.mjc.school.repository.filter.specification.SearchCriteria;
import com.mjc.school.repository.filter.specification.SearchOperation;
import com.mjc.school.service.filter.ResourceSearchFilter;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseSearchFilterMapper<T> {

    public final static String SORT_AND_FILTER_DELIMITER = ":";

    public final static String MULTIPLE_VALUE_DELIMITER = ";";

    protected final static SortOrder ASC_ORDER = SortOrder.ASC;

    protected final static SortOrder DESC_ORDER = SortOrder.DESC;

    protected final static Map<String, String> DEFAULT_SORTING_MAP = new HashMap<>();

    public List<Sorting> getDefaultSorting() {
        return DEFAULT_SORTING_MAP.entrySet().stream()
            .map(e -> new Sorting(e.getKey(), SortOrder.valueOf(e.getValue())))
            .collect(Collectors.toList());
    }

    public abstract ResourceSearchFilter map(T searchFilterRequest);

    protected ResourceSearchFilter createResourceSearchFilter(final int page, final int pageSize, final List<String> sortAndOrder, final List<String> searchFilterRequest) {
        Pagination pagination = new Pagination(page, pageSize);
        List<Sorting> sorting = createSorting(sortAndOrder);
        List<SearchCriteria> searchCriteriaList = createSearchCriteriaList(searchFilterRequest);
        return new ResourceSearchFilter(pagination, sorting, searchCriteriaList);
    }

    protected List<Sorting> createSorting(List<String> sorting) {
        List<Sorting> sortingList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sorting)) {
            return getDefaultSorting();
        }
        for (String sort : sorting) {
            String[] splitSorting = sort.split(SORT_AND_FILTER_DELIMITER);
            sortingList.add(new Sorting(splitSorting[0], SortOrder.valueOf(splitSorting[1].toUpperCase())));
        }
        return sortingList;
    }

    protected List<SearchCriteria> createSearchCriteriaList(final List<String> searchFilter) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        if (CollectionUtils.isEmpty(searchFilter)) {
            return Collections.EMPTY_LIST;
        }
        for (String filter : searchFilter) {
            String[] splitFilter = filter.split(SORT_AND_FILTER_DELIMITER);
            if (splitFilter.length == 3) {
                searchCriteriaList.add(
                    new SearchCriteria(splitFilter[0], SearchOperation.getSearchOperationByName(splitFilter[1]), splitFilter[2]));
            }
        }
        return searchCriteriaList;
    }
}
