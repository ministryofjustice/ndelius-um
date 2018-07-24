package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.service.SearchService;
import uk.co.bconline.ndelius.util.SearchIndexHelper;

import java.time.LocalDateTime;

@Service
public class SearchServiceImpl implements SearchService
{
	private SearchIndexHelper searchIndexHelper;

	@Autowired
	public SearchServiceImpl(SearchIndexHelper searchIndexHelper)
	{
		this.searchIndexHelper = searchIndexHelper;
	}

	@Override
	public void reindex()
	{
		searchIndexHelper.reIndex();
		searchIndexHelper.setLastIndexed(LocalDateTime.now());
	}
}
