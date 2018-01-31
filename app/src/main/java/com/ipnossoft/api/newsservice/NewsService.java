package com.ipnossoft.api.newsservice;

import com.ipnossoft.api.newsservice.model.News;
import java.util.List;

public interface NewsService {
  void addListener(NewsServiceListener var1);

  void fetchNews();

  void flagNewsAsRead();

  List<News> getNews();

  boolean isFinishedFetchingNews();

  void removeListener(NewsServiceListener var1);

  int unreadCount();
}
