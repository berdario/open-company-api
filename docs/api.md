# Temporary OpenCompany API Documentation

## Company List

```
GET /companies
```
returns: application/vnd.collection+vnd.open-company.company+json;version=1

## Company

### Listing a company:

```
GET /companies/<slug>
GET /companies/<slug>?as-of=<timestamp>
```
returns: `application/vnd.open-company.company.v1+json`

### Creating a company:

```
PUT /companies/<slug>
```
accepts: `application/vnd.open-company.company.v1+json`
returns: `application/vnd.open-company.company.v1+json`

### Updating a company:

```
PATCH /companies/<slug>
```
accepts: `application/vnd.open-company.company.v1+json`
returns: `application/vnd.open-company.company.v1+json`

Note-worthy: `PATCH`ing just `sections` property to change order of sections

Note-worthy: `PATCH`ing just `sections` property to remove a sections

Note-worthy: `PATCH`ing `sections` and sending a new section at the same time to add a new section, eg.:
```json
{
  "sections" : {"progress": ["update", "challenges", "help", "growth", "finances"], "company": ["mission", "values"]},
  "help" : {
    "title" : "Asks",
    "body" : "<p>...</p>"
  }
}
```

### Removing a company:

```
DELETE /companies/<slug>
```

TBD what this means. All the implications.


## Sections

### Getting a section:

```
GET /companies/<slug>/<section-name>
GET /companies/<slug>/<section-name>?as-of=<timestamp>
```
returns: `application/vnd.open-company.section.v1+json`

### Creating a section:

`PATCH` the company. See updating a company above.

### Revising a section:

```
PATCH /companies/<slug>/<section-name>
```
accepts: `application/vnd.open-company.section.v1+json`
returns: `application/vnd.open-company.section.v1+json`

Note-worthy: `PATCH`ing just `body` property to revise the section content.

Note-worthy: `PATCH`ing just `title` property to revise the section's title.

Note-worthy: `PATCH`ing just `notes` property to revise the section's notes.

### Removing a section:

Remove the section name from the `sections` property of the company. See updating a company above.


## Comments

### Getting comments

Comments come back as part of sections, eg:

```
"challenges": {
  "title": "Key Challenges",
  "body": "<p>...</p>",
  "updated-at": "2015-09-14T20:49:19.000Z",
  "author": {
    "user-id": "123456",
    "image": "http://www.emoticonswallpapers.com/avatar/cartoons/Wiley-Coyote-Dazed.jpg",
    "name": "Wile E. Coyote"
  },
  "comments": [],
  "revisions": [...],
  "links": [
    {
      "rel": "comment",
      "method": "POST",
      "href": "/companies/buffer/challenges/comments",
      "type": "application/vnd.open-company.comment.v1+json"
    },
    ...
  ]
}
```

or

```
"challenges": {
  "title": "Key Challenges",
  "body": "<p>...</p>",
  "updated-at": "2015-09-14T20:49:19.000Z",
  "author": {
    "user-id": "123456",
    "image": "http://www.emoticonswallpapers.com/avatar/cartoons/Wiley-Coyote-Dazed.jpg",
    "name": "Wile E. Coyote"
  },
  "comments": [
    {
      "comment-id": "12345",
      "created-at": "2015-09-14T21:56:17.000Z",
      "updated-at": "2015-09-14T21:56:17.000Z",
      "body": "That does seem challenging.",
      "author": {
        "user-id": "234567",
        "image": "http://www.brentonholmes.com/wp-content/uploads/2010/05/albert-camus1.jpg",
        "name": "Albert Camus"
      },
      "links": [
        {
          "rel": "reply",
          "method": "POST",
          "href": "/companies/buffer/challenges/comments/12345",
          "type": "application/vnd.open-company.comment.v1+json"
        }
      ]
    },
    {
      "comment-id": "23456",
      "response-to": "12345",
      "created-at": "2015-09-14T22:45:17.000Z",
      "updated-at": "2015-09-14T22:45:17.000Z",
      "body": "No, not really.",
      "author": {
        "user-id": "123456",
        "image": "http://www.emoticonswallpapers.com/avatar/cartoons/Wiley-Coyote-Dazed.jpg",
        "name": "Wile E. Coyote"
      },
      "links": []
    }
  ], 
  "revisions": [...],
  "links": [
    {
      "rel": "comment",
      "method": "POST",
      "href": "/companies/buffer/challenges/comments",
      "type": "application/vnd.open-company.comment.v1+json"
    },
    ...
  ]
}
```

### Creating comments

Comments can be created by following the section's `comment` link, eg:

```
POST /companies/<slug>/<section-name>/comments
```
  accepts: `application/vnd.open-company.comment.v1+json`
  returns: `application/vnd.open-company.comment.v1+json`

Replies to exisitng comments can be created by following the comment's `reply` link, eg:

```
POST /companies/<slug>/<section-name>/comments/<comment-id>
```
  accepts: `application/vnd.open-company.comment.v1+json`
  returns: `application/vnd.open-company.comment.v1+json`

### Updating a comment

TBD. They'll be a link in `links`.

### Removing a comment.

TBD. They'll be a link in `links`.
