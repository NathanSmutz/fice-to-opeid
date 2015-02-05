# ficetranslation

A Data Mining project to map FICE codes to OPEID identification (using Clojure)

The university for which I work needs to make a change in its records.  We currently use obsolete FICE codes to identify other universities in our database.  We need to convert our records to use OPEID identification numbers instead.  Crosswalk tables between these systems have been elusive.  The National Center for Education Statistics has spreadsheets of survey data going back to 1980.  Three spreadsheets for years 1995-96, 1996-97, 1997-98 contain both FICE and OPEID identification numbers.
This project mines those spreadsheets for correlations between FICE and OPEID, producing a map with FICE id's as keys and sets of OPEID's as values.  (Different parts of a composite institution can have separate OPEID's, whereas FICE generally referred to a whole organization.  Hence, the one-to-many relation between FICE and OPEID).
This seemed like a good project to try out data processing with Clojure.  Of particular interest are techniques for data mining when whole of the data set is too large for working memory.

Note: Clojure is a modern Lisp running on the JVM with an emphasis on concurrency and immutable data structures: http://clojure.org/

## Usage

FIXME
