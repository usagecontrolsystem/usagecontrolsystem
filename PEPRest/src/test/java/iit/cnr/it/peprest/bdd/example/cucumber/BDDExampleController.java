package iit.cnr.it.peprest.bdd.example.cucumber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BDDExampleController {

	private boolean msgSent;

	@Autowired
	public BDDExampleController() {
		super();
	}

	public void sendMsg()
	{
		msgSent = true;
	}

	public String poke() {
		if(this.msgSent == true)
			return "nothing more to send";
		else
			return "message to send";
	}
}