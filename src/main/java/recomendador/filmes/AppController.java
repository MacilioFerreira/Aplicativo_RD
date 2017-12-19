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
import org.springframework.social.facebook.api.PostData;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/")
public class AppController {

    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    public AppController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = new FacebookTemplate("EAACEdEose0cBAFa581RQZCPqcEBrT1ZCQ4V3bINNR58b8IBe4nBlRZBBqI6Vou6HV0rQLXxViigyZCoB3xL3uGHxBwaAnNXlINFfJKZBo3ssohBI0zzcsGYQBA0ZBy5VZAubqVq5BGlJ0hOZCuy97wjpniNdvcS7FaGj4ZBULDBrZByG7SUgqT6p8xtqEtyHDIefLaWJm1MU8VoQZDZD");
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
    
    @GetMapping(path="/shareMovie/{movie}")
    public ModelAndView shareMovie(@PathVariable String movie) {   
    	
    	ModelAndView model = new ModelAndView("shareMovie");
    	model.addObject("movie", movie);
    	return model;
    }
           
    @GetMapping(path="/share")
    public String shareMoviePage(String movie) {

    	PostData post = new PostData("me").link("https://recomendadorfilme.herokuapp.com/shareMovie/"+movie, 
    			"http://www.psdgraphics.com/wp-content/uploads/2009/11/clapboard.jpg", 
    			"Página de filme recomendada: "+movie, 
    			"Trabalho da disciplina de redes sociais", 
    			"O site https://recomendadorfilme.herokuapp.com/ me recomendou a página de filme: "+movie);
    	
    	facebook.feedOperations().post(post);

    	return "redirect:/listMovies";
    }
    
	    
	    

}