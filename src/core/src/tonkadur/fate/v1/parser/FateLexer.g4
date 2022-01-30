lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;
fragment US: '_'?;
fragment IMP: '!';
WS: SEP;

L_PAREN: '(';
R_PAREN: ')';

IMP_MARKER: IMP;


FATE_VERSION_KW: L_PAREN 'fate'US'version' SEP+;


DECLARE_ALIAS_TYPE_KW:
   L_PAREN
      ((('declare'|('def''ine'?))US(('sub'|'alias')US)?'type')|'typedef')
   SEP+;
DECLARE_STRUCT_TYPE_KW:
   L_PAREN ('declare'|('def''ine'?))US('struct''ure'?)(US'type')? SEP+;
DECLARE_EXTRA_INSTRUCTION_KW:
   L_PAREN ('declare'|('def''ine'?))US'extra'US'instruction' SEP+;
DECLARE_EXTRA_COMPUTATION_KW:
   L_PAREN ('declare'|('def''ine'?))US'extra'US'computation' SEP+;
DECLARE_EXTRA_TYPE_KW:
   L_PAREN ('declare'|('def''ine'?))US'extra'US'type' SEP+;
DECLARE_EVENT_TYPE_KW:
   L_PAREN ('declare'|('def''ine'?))(US'input')?US'event'(US'type')? SEP+;
DECLARE_TEXT_EFFECT_KW:
   L_PAREN ('declare'|('def''ine'?))US'text'US'effect' SEP+;
DECLARE_GLOBAL_VARIABLE_KW: L_PAREN 'global' SEP+;
DECLARE_EXTERNAL_VARIABLE_KW: L_PAREN 'extern'('al'?) SEP+;
DECLARE_INSTRUCTION_KW:
   L_PAREN ('declare'|('def''ine'?))US'instruction' SEP+;
DECLARE_COMPUTATION_KW:
   L_PAREN ('declare'|('def''ine'?))US'computation' SEP+;

DEFINE_SEQUENCE_KW:
   L_PAREN
      ('declare'|('def''ine'?))US(('seq'('uence'?))|('proc'('edure'?)))
   SEP+;

IMP_IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning')'!' SEP+;
REQUIRE_EXTENSION_KW: L_PAREN 'require'US'extension' SEP+;
REQUIRE_KW: L_PAREN 'require' SEP+;
INCLUDE_KW: L_PAREN 'include' SEP+;




DECLARE_LOCAL_VARIABLE_KW: L_PAREN 'local' SEP+;

VISIT_KW:
   L_PAREN
   (
      ('call'|'visit') US ((('seq'|'Seq')'uence'?)|('proc'|'Proc')'edure'?)?
   )
   IMP SEP+;

CONTINUE_AS_KW:
   L_PAREN
   (
      (
         'jump'
         | ('jump' US 'to')
         | ('jumpTo')
         | ('continue' US 'as')
         | ('continueAs')
         | ('continue' US 'to')
         | ('continueTo')
         | ('continue' US 'with')
         | ('continueWith')
         | ('go' US 'to')
         | ('goTo')
      )
      (US ((('seq'|'Seq')'uence'?)|('proc'|'Proc')'edure'?))?
   )
   IMP SEP+;


VARIABLE_KW: L_PAREN ('variable'|'var') SEP+;

ENABLE_TEXT_EFFECT_KW: L_PAREN 'text'US'effect' SEP+;

TEXT_KW: L_PAREN 'text' SEP+;

COND_KW: L_PAREN 'cond' SEP+;
DO_WHILE_KW: L_PAREN 'do'US'while' SEP+;
FOR_KW: L_PAREN 'for' SEP+;
FOR_EACH_KW: L_PAREN 'for'US'each' SEP+;
WHILE_KW: L_PAREN 'while' SEP+;
SWITCH_KW: L_PAREN 'switch' SEP+;

IF_ELSE_KW: L_PAREN 'if'US'else' SEP+;
IF_KW: L_PAREN 'if' SEP+;


STRING_KW: L_PAREN 'string' SEP+;


DEFAULT_KW: L_PAREN 'default' SEP+;
CAST_KW: L_PAREN 'cast' SEP+;



IMP_ASSERT_KW: L_PAREN 'assert!' SEP+;
IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning') SEP+;




//EXTENSION_FIRST_LEVEL_KW: L_PAREN '@';
//EXTRA_INSTRUCTION_KW: L_PAREN '#';
//EXTRA_COMPUTATION_KW: L_PAREN '$';


FIELD_ACCESS_KW: L_PAREN 'struct:'(('get'(US'field')?)|'field') SEP+;
SET_FIELDS_KW: L_PAREN 'struct:set'(US'fields')? SEP+;
IMP_SET_FIELDS_KW: L_PAREN 'struct:set'((US'fields!')|'!') SEP+;



PLAYER_CHOICE_KW:
   L_PAREN ('choice'|'user'US'choice'|'player'US'choice')'!' SEP+;
TEXT_OPTION_KW: L_PAREN ('option'|'user'US'option'|'player'US'option') SEP+;
EVENT_OPTION_KW: L_PAREN ('event'|'user'US'event'|'player'US'event') SEP+;
PROMPT_STRING_KW: L_PAREN 'prompt'US'str''ing'?'!' SEP+;
PROMPT_FLOAT_KW: L_PAREN 'prompt'US'float!' SEP+;
PROMPT_INTEGER_KW: L_PAREN 'prompt'US'int''eger'?'!' SEP+;
PROMPT_COMMAND_KW: L_PAREN 'prompt'US('cmd'|'command')'!' SEP+;




LET_KW: L_PAREN 'let' SEP+;

LAMBDA_KW: L_PAREN 'lambda' SEP+;

SEQUENCE_KW: L_PAREN 'seq''uence'? SEP+;


fragment IDENTIFIER_FRAG: ~([ \t\r\n()]|'!'|';');

IDENTIFIER_KW: IDENTIFIER_FRAG+;

WORD: (IDENTIFIER_FRAG|'!'|'(lp)'|'(rp)'|'(sp)'|'(2;)'|';')+
   {
      setText
      (
         getText().replaceAll
         (
            "\\(sp\\)",
            " "
         ).replaceAll
         (
            "\\(lp\\)",
            "("
         ).replaceAll
         (
            "\\(rp\\)",
            ")"
         ).replaceAll
         (
            "\\(2;\\)",
            ";;"
         )
      );
   };


COMMENT: WS* ';;' .*? '\n' -> channel(HIDDEN);
