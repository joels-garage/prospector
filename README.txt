Prospector

This is the main project; this file is for random notes.

TODO

# get a copy of dom4j with source.

* DIALOG DESIGN

Write a state machine for the dialog.

Use a state-machine library -- http://commons.apache.org/scxml/ looks pretty good.


That's for IM, and the beginner web UI.

For email, it's a different thing, I think.  Dunno.

* back up the db, dump it through the ORM
* do email/jabber interface
* fill it up with content, do some web parsing

* conversational UI, one question, one answer, per screen
** what would you like to talk about?
** answers become assertions -- verify them
** some answers are pick-from-list things
** what are some examples of <class>?

* ask "what are you choosing?"
** find the class by name, (e.g. "lightbulb").  (or is it an individual?)
** look at the "purpose" attribute of that class (a class axiom, or just a fact?)
** suggest other classes with the same value (e.g. "makes light")

in the GUI, when I open a panel, it should populate the lookup list.

in the GUI, suggest a default name for simple entities (e.g. stakeholder)

add "IndividualUtility" to PreferencePane pulldown.

validation!  value >0 <1, required-fields, etc.  make some "validator" classes?  or make the model do it?
render invalid fields as red, and grey out the 'save' button

keep a pane of ranked alternatives up while data is being entered?

filter fact "subject" and "object" selection by property range class, after it's selected

alternativecollection => GWT ui

fill with content.  crawl a few websites in a targeted way (is there some open source for that?)

email/jabber interface: "whachaneed?" answer-class, find by name, look at "purpose" axiom on the class (another class),
then look at members of that, and suggest them as alternatives (e.g. lightbulb -> lighting)

back up the db (dump and load through hibernate, don't do mysql version), also schema migration, how to do it.

OK, got the direct, implied, utility-derived, and property-derived.

next need to do reverse(DONE), reverse-property(DONE), implied-property,
and also iterations of implication on both individuals and properties.



sensitivity, several kinds, all in terms of utility or score.  ignore multidimensional effects.
* preference sensitivity.  if you liked vanilla more, you would agree with your friends
* property sensitivity.  if you found an individual with property x, that would be the one
* weight sensitivity.  if you cared more about horsepower, you would agree with your friends
don't care about changes that don't affect the top-n (for what n?) of the list.

consensus measure
* for multiple stakeholders: measures agreement, an intrinsic value
* for single stakeholder, multiple attributes, measures "mixed bag" vs "all good"

URL entry/parsing
* copy the message into the mirror, or make a new message (type) for the data-entry screen
* create an unnamed entity (the URL is assumed to describe it)
* create evidence linking the URL span (href) to the entity

An Email mirror, to retrieve messages from IMAP
a web mirror, to get BBS pages

define the notion of "question" and "answer" for high-sensitivity unknowns

derived preferences need provenance.

Some issue in the preference pane; its list widget doesn't update correctly.

Finish "measurement type" representation.

When filling out a fact, or a preference, the lookup "find"s should be restricted by the type
specified by the corresponding property.  If the property isn't filled in yet, don't restrict, but
if it is, then do.

Add some padding to the layouts.

Do some sort of "working" label near the top of the page; make the RPCs register and deregister with it, and time them
out somehow.  Slow RPCs need an "indicator" of some kind.


ok, so, i like the "AnyPropertyWidget" container thing.  Finish making it work; this means fixing
up the other bits that assume the base class is instantiable, e.g. modelentityeditorpanel, and i'm sure
some other fixing.




ok, so, now i have the polymorphic rpc and polymorphic hibernate working.  seems kinda slow but whatever.

so now (tomorrow), finally, do the fact and property forms.

OK, so that's half done now.  Finish preferences tomorrow, start entering facts.




Someday, do the color-space model thing that I never got a chance to do (i.e. color = radiation energy not symbol).

Note that "fact" records require type-checking by property, so, make fact entry a per-individual form,
parameterized by the properties on the individual's class.

Do something about class-class relations (e.g. subclass).

Implement Fact.



Make a click-through browse interface, starting at a decision list.

Add a "no items" row in the listtable that spans all the rows, just so there's some indication.


Add "delete" as a list operator not just an editor operator.

What to do about cascading deletes, i.e. orphaned items?  Or equivalently, renaming/repurposing of
existing entities?  Maybe disallow renaming?  Or just show the history?  Disallow deletes?

Allow picking from the table, not just the list.

implement search-by-field, e.g. individuals by class, not just by name.

 
 future TODO's
 

 * create decision; including inviting stakeholders by email
 * create option: url, name
 * create preference: for now, just "love/hate"
 * view decision-list: mine and visible-to-me, ranked by recent activity (e.g. other views)
 * view a single decision
 * daily spam with my decision list
 * questions: user A makes a new option -> ask user B (another stakeholder) to evaluate it.
   * is this an abstract question or just a case-by-case thing?
 * build MALLET for fun.
 
 LATER:
 
 * do email confirmation flow, which means SMTP and IMAP.
 * implement long-list pagination.  there must be some standard hibernate trick for that.
 * assign "creator" field correctly -- for now, just leave it null.