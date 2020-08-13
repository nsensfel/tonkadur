lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;
fragment US: '_'?;

WS: SEP;

L_PAREN: '(';
R_PAREN: ')';


ABS_KW: L_PAREN 'abs'('olute'?) SEP+;
ACCESS_KW: L_PAREN 'access' SEP+;
ADD_KW: L_PAREN 'add'(US'element')? SEP+;
AND_KW: L_PAREN ('and'|'/\\') SEP+;
ASSERT_KW: L_PAREN 'assert' SEP+;
AT_KW: L_PAREN 'at' SEP+;
BREAK_KW: L_PAREN 'break)';
CAST_KW: L_PAREN 'cast' SEP+;
CLEAR_KW: L_PAREN 'clear' SEP+;
COND_KW: L_PAREN 'cond' SEP+;
COUNT_KW: L_PAREN 'count' SEP+;
DECLARE_ALIAS_TYPE_KW:
   L_PAREN ((('declare'|'define'|'def')US('sub'US)?'type')|'typedef') SEP+;
DECLARE_DICT_TYPE_KW: L_PAREN ('declare'|'define'|'def')US('dict'|'struct')(US'type')? SEP+;
DECLARE_EVENT_TYPE_KW: L_PAREN ('declare'|'define'|'def')US'event'(US'type')? SEP+;
DECLARE_TEXT_EFFECT_KW: L_PAREN ('declare'|'define'|'def')US'text'US'effect' SEP+;
DECLARE_VARIABLE_KW: L_PAREN 'global' SEP+;
LOCAL_KW: L_PAREN 'local' SEP+;
DEFINE_SEQUENCE_KW: L_PAREN ('declare'|'define'|'def')US'seq'('uence')? SEP+;
DIVIDE_KW: L_PAREN ('divide'|'/'|'div') SEP+;
DO_WHILE_KW: L_PAREN ('do'US'while') SEP+;
ENABLE_TEXT_EFFECT_KW: L_PAREN 'text'US'effect' SEP+;
END_KW: L_PAREN 'end)';
EQUALS_KW: L_PAREN ('equals'|'='|'=='|'eq') SEP+;
EVENT_KW: L_PAREN 'event' SEP+;
EXTENSION_FIRST_LEVEL_KW: L_PAREN '@';
EXTENSION_INSTRUCTION_KW: L_PAREN '#';
EXTENSION_VALUE_KW: L_PAREN '$';
FALSE_KW: L_PAREN 'false)';
IGNORE_ERROR_KW: L_PAREN 'ignore'US('error'|'warning') SEP+;
FATE_VERSION_KW: L_PAREN 'fate'US'version' SEP+;
FIELD_KW: L_PAREN 'field' SEP+;
FOR_EACH_KW: L_PAREN ('for'US'each') SEP+;
FOR_KW: L_PAREN 'for' SEP+;
FREE_KW: L_PAREN ('free'|'release'|'destroy') SEP+;
GREATER_EQUAL_THAN_KW: L_PAREN ('greater'US'equal'US'than'|'>='|'ge') SEP+;
GREATER_THAN_KW: L_PAREN ('greater'US'than'|'>'|'gt') SEP+;
IF_ELSE_KW: L_PAREN ('if'US'else') SEP+;
IF_KW: L_PAREN 'if' SEP+;
IMPLIES_KW: L_PAREN ('implies'|'=>'|'->') SEP+;
INCLUDE_KW: L_PAREN 'include' SEP+;
INDEX_OF_KW: L_PAREN ('index'US'of') SEP+;
IS_MEMBER_KW: L_PAREN ('is'US'member'|'contains'|'has') SEP+;
LOWER_EQUAL_THAN_KW: L_PAREN ('lower'US'equal'US'than'|'=<'|'<='|'le') SEP+;
LOWER_THAN_KW: L_PAREN ('lower'US'than'|'<'|'lt') SEP+;
MINUS_KW: L_PAREN ('minus'|'-') SEP+;
MIN_KW: L_PAREN ('min'('imum'?)) SEP+;
MAX_KW: L_PAREN ('max'('imum'?)) SEP+;
LAMBDA_KW: L_PAREN 'lambda' SEP+;
EVAL_KW: L_PAREN 'eval'('uate'?) SEP+;
CLAMP_KW: L_PAREN ('clamp') SEP+;
MODULO_KW: L_PAREN ('modulo'|'%'|'mod') SEP+;
NEWLINE_KW: L_PAREN 'newline)';
NEW_KW: L_PAREN ('new'|'reserve'|'create') SEP+;
NOT_KW: L_PAREN ('not'|'~'|'!') SEP+;
ONE_IN_KW: L_PAREN ('exactly'US)?'one'(US'in')? SEP+;
OR_KW: L_PAREN ('or'|'\\/') SEP+;
RICH_TEXT_KW: L_PAREN (('rich'US)?'text') SEP+;
PLAYER_CHOICE_KW: L_PAREN ('choice'|'user'US'choice'|'player'US'choice') SEP+;
PLUS_KW: L_PAREN ('plus'|'+') SEP+;
POWER_KW: L_PAREN ('power'|'^'|'**'|'pow') SEP+;
RANDOM_KW: L_PAREN ('random'|'rand'|'rnd') SEP+;
REF_KW: L_PAREN (((('ref'('erence'?))|'ptr'|'pointer')(US'to')?)|('address'(US'of'))) SEP+;
REMOVE_ALL_KW: L_PAREN 'remove'US'all' SEP+;
REVERSE_KW: L_PAREN 'reverse'(US'list')? SEP+;
REMOVE_ONE_KW: L_PAREN 'remove'US'one' SEP+;
REMOVE_AT_KW: L_PAREN ('remove'US('elem'('ent')?US)?'at'|'rm'|'del'|'delete') SEP+;
REQUIRE_EXTENSION_KW: L_PAREN 'require'US'extension' SEP+;
REQUIRE_KW: L_PAREN 'require' SEP+;
SEQUENCE_KW: L_PAREN 'seq'('uence')? SEP+;
SET_FIELDS_KW: L_PAREN 'set'US'fields' SEP+;
SET_KW: L_PAREN 'set' SEP+;
LIST_KW: L_PAREN 'list' SEP+;
SIZE_KW: L_PAREN 'size' SEP+;
SWITCH_KW: L_PAREN 'switch' SEP+;
TIMES_KW: L_PAREN ('times'|'*') SEP+;
TRUE_KW: L_PAREN 'true)';
DONE_KW: L_PAREN 'done)';
VAL_KW: L_PAREN ('val'|'value') SEP+;
VARIABLE_KW: L_PAREN ('variable'|'var') SEP+;
VISIT_KW: L_PAREN ('call'|'visit')(US(('seq'('uence'?))|('proc'('edure'?))))? SEP+;
CONTINUE_AS_KW: L_PAREN (('continue'US('as'|'to'|'with'))|('jump'(US'to')?)|('go'US'to')|'exec')(US(('seq'('uence'?))|('proc'('edure'?))))? SEP+;
WHILE_KW: L_PAREN 'while' SEP+;

WORD: (~([ \t\r\n()])|'\\)'|'\\(')+;

COMMENT: WS* ';' .*? '\n' -> channel(HIDDEN);
