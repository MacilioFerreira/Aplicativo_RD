package recomendador.filmes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class AppController {

    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public AppController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = new FacebookTemplate("EAACEdEose0cBADPIDDdNFA94yGG3pv0OXRbUGhuX54uIPli5H5DYno36kFbh27QZCyWlVpCcDaeM9AowFjAH39gSNzAod6R8UxXDuSXsv4jMvf0WUeoEQKTdnxhLRsyvGMOHsKWF3sE0YPv5BjsPZBEAlyoHvNXUKZCwiq0Cp0HYvOR1cJuwZBZCVKFsspJb5JrqvJtcdeAZDZD");
        this.connectionRepository = connectionRepository;
    }

    @GetMapping
    public String helloFacebook(Model model) {
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            return "redirect:/connect/facebook";
        }
        
//        String [] fields = { "id", "email",  "first_name", "last_name" };
//        User userProfile = facebook.fetchObject("me", User.class, fields);
//        
//        model.addAttribute("facebookProfile", userProfile);
//        model.addAttribute("feed", fields);
        return "redirect:/listMovies";
    }
    
    @GetMapping(path="/listMovies")
    public String listarMeusFilmes(Model model) {
        PagedList<Page> myMovies = facebook.likeOperations().getMovies();
        List<Movie> movies = new ArrayList<Movie>();
        
        for (Page page : myMovies) {
        	Movie movie = new Movie(page.getName(), page.getCategory(), page.getProducedBy());
        	movies.add(movie);
		}        
        model.addAttribute("movies", movies);
    	return "listMovies";
    	
    }
    
    @GetMapping(path="/buscarFilme")
    public String recomendarFilme(Model model) {
    	
		PagedList<Reference> amigos = facebook.friendOperations().getFriends();
		PagedList<Page> myMovies = facebook.likeOperations().getMovies();
		PagedList<Page> movies =  facebook.likeOperations().getMovies(amigos.get(0).getId());
		        
		for (int i =1; i < amigos.size(); i++) {
			PagedList<Page> amigoMovies = facebook.likeOperations().getMovies(amigos.get(i).getId());
			for (Page amigoFilme : amigoMovies) {
				if (!myMovies.contains(amigoFilme)) {
					movies.add(amigoFilme);
				}
			}        	
		}
		        
		long [] a = new long[movies.size()];
	    for (int i = 0; i < a.length; i++) {
	        a[i] = Long.parseLong(movies.get(i).getId());
		}
	    
	    Map<Long, Integer> map = new HashMap<Long, Integer>();
	    for (long i : a) {
	        Integer count = map.get(i);
	        map.put(i, count != null ? count+1 : 0);
	    }
	    
	    Long popular = Collections.max(map.entrySet(),
	        	    new Comparator<Map.Entry<Long, Integer>>() {
	        	    @Override
	        	    public int compare(Entry<Long, Integer> o1, Entry<Long, Integer> o2) {
	        	        return o1.getValue().compareTo(o2.getValue());
	        	    }
	        	}).getKey();
	    
	    Movie movie_popular = new Movie();
	    for (Page movie : movies) {
	    	if(movie.getId().equals(String.valueOf(popular))) {
	    		movie_popular.setTitle(movie.getName());
	    		movie_popular.setCategory(movie.getCategory());
	    		movie_popular.setProducer(movie.getProducedBy());
	    		break;
	    	}
	    }
	    
	    List<Movie> movies_ = new ArrayList<Movie>();
        
        for (Page page : myMovies) {
        	Movie movie = new Movie(page.getName(), page.getCategory(), page.getProducedBy());
        	movies_.add(movie);
		}        
        model.addAttribute("movies", movies_);
		model.addAttribute("movie", movie_popular);
	    return "listMovies";
	    
    }
    
    @GetMapping(path="/compartilharFilme")
    public String compartilharFilme(@RequestParam Movie movie, Model model) {
    	
    	return "";
    }
	    
	    

}