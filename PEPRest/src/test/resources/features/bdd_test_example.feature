Feature: Is my message sent?
  I want to know if my message is sent

Scenario: Knowing there is a message, I want to see when it is sent
	Given that I have a message to send
	When I trigger deliver
	Then I receive nothing more to send