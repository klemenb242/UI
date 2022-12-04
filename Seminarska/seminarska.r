install.packages("e1071")

library(CORElearn)

# HELPERS
Split70to30 <- function(data)
{
    train <- data[1:round(0.7*nrow(data)),]
    test <- data[-(1:round(0.7*nrow(data))),]
    return(list(train, test))
}

attr <- function(position, attrName) {
    return(paste(position, attrName, sep=""));
}






# __START

set.seed(0)

setwd("C:/Users/Gal/Documents/FRI/UI/Seminarska/")
md <- read.table(file="nbadata.txt", sep=",", header=TRUE)

md <- md[order(md$gmDate),];


SEASONS = unique(md$gmSeason);
TEAMS = unique(c(md$awayAbbr, md$homeAbbr))

# # VIZUALIZACIJA
# # povprecno stevilo metov trojk obeh ekip skupaj na sezono
# averageThrees <- vector()
# for(season in SEASONS) {
#     seasonGames <- md$gmSeason == season;
#     averageThrees <- c(averageThrees, mean(md$home3PA[seasonGames] + md$away3PA[seasonGames]));
# }
# barplot(averageThrees, names=SEASONS, main="Povprecno število vseh metov za tri pike v tekmi na sezono");

# # stevilo zmag home ekip vs stevilo zmag away ekip
# homeWins = table(md$homePTS > md$awayPTS);
# pie(homeWins, labels=c("Away", "Home"), main="Zmage doma VS zmage v gostovanju", labels = round(values/sum(values)*100, 2)));


# # najboljsa ekipa po razmerju zmag
# winRatioByTeam = list();
# for (team in TEAMS) {
#     homeGames <- md$homeAbbr == team;
#     awayGames <- md$awayAbbr == team;
#     homeWins <- md$homePTS[homeGames] > md$awayPTS[homeGames];
#     awayWins <- md$homePTS[awayGames] < md$awayPTS[awayGames];
#     totalWins  <- sum(homeWins) + sum(awayWins);
#     totalGames <- sum(homeGames) + sum(awayGames);
#     ratio <- totalWins / totalGames;
#     winRatioByTeam[[team]] <- ratio;
# }
# winRatioByTeam <- sort(unlist(winRatioByTeam), decreasing=TRUE);
# barplot(winRatioByTeam[1:5], names=names(winRatioByTeam)[1:5], main="Top 5 najboljših ekip po razmerju zmag");


# # Graf odvisnosti uspesnih prostih metov od stevila prostih dni
# homeFreeShotSuccessRatio = md$homeFTM / md$homeFTA;
# awayFreeShotSuccessRatio = md$awayFTM / md$awayFTA;
# totalFreeShotSuccessRatio = append(homeFreeShotSuccessRatio, awayFreeShotSuccessRatio);
# daysOff = append(md$homeDayOff, md$awayDayOff);
# plot(totalFreeShotSuccessRatio,daysOff, main="Razmerje uspešnih prostih metov odvisno od števila prostih dni", xlab="Število prostih dni", ylab="Razmerje uspešnih metov za tri pike");





structureTeamData <- function(games, position)
{
    teamStatistics <- list();    
    teamStatistics[[attr(position, "PTS")]] <- mean(games[[attr(position, "PTS")]]);
    teamStatistics[[attr(position, "AST")]] <- mean(games[[attr(position, "AST")]]);
    teamStatistics[[attr(position, "STL")]] <- mean(games[[attr(position, "STL")]]);
    teamStatistics[[attr(position, "BLK")]] <- mean(games[[attr(position, "BLK")]]);

    teamStatistics[[attr(position, "ORBR")]] <- mean(games[[attr(position, "ORB")]] / games[[attr(position, "TRB")]]);
    teamStatistics[[attr(position, "DRBR")]] <- mean(games[[attr(position, "DRB")]] / games[[attr(position, "TRB")]]);
    teamStatistics[[attr(position, "PTS1")]] <- mean(games[[attr(position, "PTS1")]]);
    teamStatistics[[attr(position, "PTS2")]] <- mean(games[[attr(position, "PTS2")]]);
    teamStatistics[[attr(position, "PTS3")]] <- mean(games[[attr(position, "PTS3")]]);
    teamStatistics[[attr(position, "PTS4")]] <- mean(games[[attr(position, "PTS4")]]);
    
    # percetanges
    teamStatistics[[attr(position, "FGR")]] <- mean(games[[attr(position,"FGM")]] / games[[attr(position, "FGA")]]);
    teamStatistics[[attr(position, "3PR")]] <- mean(games[[attr(position,"3PM")]] / games[[attr(position, "3PA")]]);
    teamStatistics[[attr(position, "2PR")]] <- mean(games[[attr(position,"2PM")]] / games[[attr(position, "2PA")]]);
    teamStatistics[[attr(position, "FTR")]] <- mean(games[[attr(position,"FTM")]] / games[[attr(position, "FTA")]]);

    return(teamStatistics);
}

pastWinLoseRatio <- function (teamAbbr, beforeDate, data) {
    homeGamesSelection <- data$homeAbbr == teamAbbr & data$gmDate < beforeDate;
    homeGames <- data[homeGamesSelection,];
    homeWinsRatio <- mean(homeGames$homePTS > homeGames$awayPTS);
    awayGamesSelection <- data$awayAbbr == teamAbbr & data$gmDate < beforeDate;
    awayGames <- data[awayGamesSelection,];
    awayWinsRatio <- mean(awayGames$homePTS < awayGames$awayPTS);
    return ((homeWinsRatio + awayWinsRatio) / 2);
}

# vrne pozitiven rezultat ce je ekipa1 overal boljsa, negativen ce je ekipa2 overal boljsa
pastMatchesScoreDifference <- function (team1, team2, beforeDate) {
    homeGamesSelection <- md$homeAbbr == team1 & md$awayAbbr == team2 & md$gmDate < beforeDate;
    homeGames <- md[homeGamesSelection,];
    homeGamesScoreDifference <- sum(homeGames$homePTS - homeGames$awayPTS);
    awayGamesSelection <- md$homeAbbr == team2 & md$awayAbbr == team1 & md$gmDate < beforeDate;
    awayGames <- md[awayGamesSelection,];
    awayGamesScoreDifference <- sum(awayGames$awayPTS - awayGames$homePTS);
    totalSum = homeGamesScoreDifference + awayGamesScoreDifference;
    if (is.null(totalSum)) {
        return(0);
    }
    return (totalSum);
}



# slice md to only 300 games
#  md <- md[1:300,];

# structuredData <- data.frame();
# for (i in 1:nrow(md)) {
#     game <- md[i,];
#     homeTeamGamesSelection <- md$homeAbbr == game$homeAbbr & md$gmDate < game$gmDate;
#     homeTeamGames <- md[homeTeamGamesSelection,];
#     if (nrow(homeTeamGames) == 0) {
#         next;
#     }
#     structuredHomeTeamData = structureTeamData(homeTeamGames, "home");
#     # dodan nov atribut zxwwa ratio vseh preteklih zmag
#     structuredHomeTeamData$homeWins <- pastWinLoseRatio(game$homeAbbr, game$gmDate, md);

#     awayTeamGamesSelection <- md$awayAbbr == game$awayAbbr & md$gmDate < game$gmDate;
#     awayTeamGames <- md[awayTeamGamesSelection,];
#     if (nrow(awayTeamGames) == 0) {
#         next;
#     }
#     structuredAwayTeamData = structureTeamData(awayTeamGames, "away");
#     structuredAwayTeamData$awayWins <- pastWinLoseRatio(game$awayAbbr, game$gmDate, md);
    
#     # zdruzeno v vrstico
#     structuredGameData <- c(structuredHomeTeamData, structuredAwayTeamData);

#     structuredGameData$pastMatchesScoreDifference <- pastMatchesScoreDifference(game$homeAbbr, game$awayAbbr, game$gmDate);
#     structuredGameData$isHomeWinner <- game$homePTS > game$awayPTS;

#     structuredData <- rbind(structuredData, structuredGameData);
# }



# <DEBUG>
structuredData <- read.csv("myfile.csv");
print(nrow(structuredData))

# </DEBUG>

structuredData$isHomeWinner <- as.factor(structuredData$isHomeWinner);

# # GainRatio omili precenjevanje vecvrednostih attributov
# informationGain <- sort(attrEval(isHomeWinner ~ ., structuredData, "InfGain"), decreasing = TRUE)
# # na podlagi analize atributov sva odstranila PTSEx, DayOff, TO in PF

splitData = Split70to30(structuredData);
train <- splitData[[1]];
test <- splitData[[2]];


# EVALUATION HELPERS
# odstotek pravilno klasificiranih pozitivnih primerov
Sensitivity <- function(obs, pred, pos.class)
{
    TP <- sum(obs == pos.class & pred == pos.class)
    FN <- sum(obs == pos.class & pred != pos.class)
    return(TP / (TP + FN))
}
# odstotek pravilno klasificiranih negativnih primerov
Specificity <- function(obs, pred, pos.class)
{
    TN <- sum(obs != pos.class & pred != pos.class)
    FP <- sum(obs != pos.class & pred == pos.class)
    return(TN / (TN + FP))
}
# odstotek pravilno klasificiranih primerov, ki so bili klasificirani kot pozitivni
Precision <- function(obs, pred, pos.class)
{
    TP <- sum(obs == pos.class & pred == pos.class)
    FP <- sum(obs != pos.class & pred == pos.class)
    return(TP / (TP + FP))
}

CA <- function(observed, predicted)
{
	mean(observed == predicted)
}

brierScore <- function(observedMatrix, predictedMatrix)
{
	sum((observedMatrix - predictedMatrix) ^ 2) / nrow(predictedMatrix)
}
library(nnet)
obsMat <- class.ind(test$isHomeWinner)


# # DECISION TREE
# library(rpart)
# library(rpart.plot)
# dt <- rpart(isHomeWinner ~ ., data=train, cp=0)
# rpart.plot(dt)

# # rpart med gradnjo drevesa interno ocenjuje njegovo kvaliteto 
# printcp(dt)
# tab <- printcp(dt)

# # izberemo vrednost parametra cp, ki ustreza minimalni napaki internega presnega preverjanja
# row <- which.min(tab[,"xerror"])
# th <- mean(c(tab[row, "CP"], tab[row-1, "CP"]))
# th

# # porezemo drevo z izbrano nastavitvijo
# dt <- prune(dt, cp=th)
# rpart.plot(dt)

# predicted <- predict(dt, test, type="class")
# CA(observed, predicted)


# NAIVNI BAYESOV KLASIFIKATOR

# library(e1071)

# nb <- naiveBayes(isHomeWinner ~ ., data = train)
# predicted <- predict(nb, test, type="class")

# CAnb <- CA(observed, predicted);

# predMat <- predict(nb, test, type = "raw")
# brierScore(obsMat, predMat)


# RANDOM FOREST

# library(randomForest)
# rf <- randomForest(position ~ ., data = train)
# predicted <- predict(rf, test, type="class")
# CA(observed, predicted)

# predMat <- predict(rf, test, type = "prob")
# brier.score(obsMat, predMat)









